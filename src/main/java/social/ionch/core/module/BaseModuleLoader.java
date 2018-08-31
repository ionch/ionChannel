/*
 * This file is part of ionChannel.
 *
 * ionChannel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * ionChannel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ionChannel.  If not, see <https://www.gnu.org/licenses/>.
 */

package social.ionch.core.module;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import social.ionch.api.module.EnableState;
import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleLoader;
import social.ionch.core.IonChannel;

public class BaseModuleLoader implements ModuleLoader {
	@GuardedBy("mutex")
	private HashMap<String, BaseModuleMetadata> modules = new HashMap<>();
	@GuardedBy("mutex")
	private BiMap<String, Module> enabled = HashBiMap.create();
	
	//private static Executor OPERATION_QUEUE = Executors.newFixedThreadPool(1);
	private Object mutex = new Object();
	
	@Nullable
	public Module getModule(String id) {
		synchronized(mutex) {
			return enabled.get(id);
		}
	}
	
	public boolean registerModule(BaseModuleMetadata meta, boolean enable) {
		synchronized(mutex) {
			//Cannot enable two modules with the same ID
			if (modules.containsKey(meta.getId())) return false;
			
			modules.put(meta.getId(), meta);
		}
		
		if (enable) {
			enableModule(meta);	
		}
		
		return true;
	}
	
	public boolean enableModule(BaseModuleMetadata meta) {
		EnableState state = meta.getState();
		if (state == EnableState.ENABLING | state == EnableState.ENABLED) return true;
		if (state == EnableState.DISABLING) return false;
		
		meta.setEnableState(EnableState.ENABLING);
		meta.getModule().enable(meta).addListener(()->{
			meta.setEnableState(EnableState.ENABLED);
			synchronized(mutex) {
				enabled.put(meta.getId(), meta.getModule());
			}
		}, MoreExecutors.directExecutor());
		
		return true;
	}

	@Override
	public Set<String> getAvailableModules() {
		synchronized(mutex) {
			return ImmutableSet.copyOf(modules.keySet());
		}
	}

	@Override
	public Set<String> getEnabledModules() {
		synchronized(mutex) {
			return ImmutableSet.copyOf(enabled.keySet());
		}
	}

	@Override
	public EnableState getModuleState(String id) {
		synchronized(mutex) {
			BaseModuleMetadata meta = modules.get(id);
			if (meta==null) return EnableState.DISABLED;
			return meta.getState();
		}
	}
	
	@Override
	public boolean isEnabled(String id) {
		return getModuleState(id)==EnableState.ENABLED;
	}
	
	@Override
	public boolean isEnabled(Module m) {
		synchronized(mutex) {
			String id = enabled.inverse().get(m);
			if (id==null) return false;
			BaseModuleMetadata meta = modules.get(id);
			if (meta==null) return false;
			return meta.getState()==EnableState.ENABLED;
		}
	}

	@Override
	public void sendMessage(String moduleId, String name, String content) {
		Module m = getModule(moduleId);
		if (m==null) return;
		m.receiveMessage(name, content, false);
	}

	@Override
	public void sendGlobalMessage(String name, String content) {
		//Freeze a momentary list of modules
		ImmutableList<Module> cur;
		synchronized(mutex) {
			cur = ImmutableList.copyOf(enabled.values());
		}
		
		//Send a global message to the frozen list.
		for(Module m : cur) {
			if (isEnabled(m)) { //Needed because a module in the frozen list might be disabled while message dispatch is happening
				m.receiveMessage(name, content, true);
			}
		}
	}
	
	public void constructBuiltin(Module m, String metadata) {
		BaseModuleMetadata meta = new BaseModuleMetadata(this.getClass().getClassLoader(), m);
		meta.setEnableState(EnableState.DISABLED);
		
		//TODO: Jankson unpack the metadata
		try {
			JsonObject metadataObject = Jankson.builder().build().load(metadata);
			String moduleId = metadataObject.get(String.class, "id");
			if (moduleId==null) {
				IonChannel.LOG.error("Couldn't load builtin module '%s': no 'id' property specified.", m.getClass().getCanonicalName());
			}
			
			meta.setId(moduleId);
		} catch (SyntaxError e) {
			IonChannel.LOG.error("Couldn't load builtin module '"+m.getClass().getCanonicalName()+"'");
			IonChannel.LOG.error("    "+e.getCompleteMessage());
		}
		String id = "foo";
		
		synchronized(mutex) {
			modules.put(id, meta);
		}
	}
	
	public void enableAll() {
		ImmutableList<BaseModuleMetadata> toEnable;
		synchronized(modules) {
			toEnable = ImmutableList.copyOf(modules.values());
		}
		
		for(BaseModuleMetadata meta : toEnable) {
			enableModule(meta);
		}
		
	}
}
