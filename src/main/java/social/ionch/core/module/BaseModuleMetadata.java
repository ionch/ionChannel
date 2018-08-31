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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import social.ionch.api.module.EnableState;
import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;

public class BaseModuleMetadata implements ModuleMetadata {
	private String id;
	private String name;
	private String[] contributors;
	private EnableState state = EnableState.DISABLED;
	private Module module;
	
	private ClassLoader classLoader;
	private Logger logger;

	/* package-private */ BaseModuleMetadata(ClassLoader loader, Module module) {
		this.classLoader = loader;
		this.module = module;
	}
	
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getContributors() {
		return contributors;
	}

	@Override
	public EnableState getState() {
		return state;
	}

	@Override
	public Module getModule() {
		return module;
	}
	
	@Override
	public Logger getLogger() {
		return logger;
	}
	
	
	/* package-private */ void setEnableState(EnableState state) {
		this.state = state;
	}
	
	/* package-private */ ClassLoader getClassLoader() {
		return this.classLoader;
	}
	
	/* package-private */ void setId(String id) {
		this.id = id;
		this.logger = LoggerFactory.getLogger("IonChannel/modules/"+id);
	}
}
