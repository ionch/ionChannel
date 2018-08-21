package social.ionch.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import social.ionch.core.api.EnableState;
import social.ionch.core.api.Module;
import social.ionch.core.api.ModuleMetadata;

public class ModuleLoader extends SecureClassLoader implements Closeable, AutoCloseable, ModuleMetadata {
	private File moduleFile;
	private JarFile jar;
	private List<String> availableClasses = new ArrayList<>();
	private Map<String, Class<?>> resolvedClasses = new HashMap<>();
	private EnableState state = EnableState.DISABLED;
	private Logger moduleLog;
	
	private Module module;
	
	private String id;
	private String name;
	private String mainClassName;
	private String[] authors;
	
	
	
	public ModuleLoader(File moduleFile) throws PluginException {
		boolean foundModuleData = false;
		
		
		this.moduleFile = moduleFile;
		try {
			jar = new JarFile(moduleFile);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				
				if (entry.isDirectory()) continue;
				
				if (entry.getName().equals("module.jkson")) {
					//This is our module metadata. Check to find the main class
				} else if (entry.getName().endsWith(".class")) {
					
				}
			}
			
		} catch (IOException ex) {
			throw new PluginException.Load("Modules must be valid jar files.", ex);
		}
		
		
		if (!foundModuleData) throw new PluginException.Load("Modules must contain a module.jkson file to identify their id and main class.");
		if (id==null) throw new PluginException.Load("The module.jkson file must specify an id");
		if (mainClassName==null) throw new PluginException.Load("The module '"+id+"' must specify a main class in its module.jkson file.");
	}
	
	@Override
	public void close() {
		try {
			jar.close();
			
			module = null;
			state = EnableState.DISABLED;
			resolvedClasses.clear();
		} catch (Throwable t) {
			//duck out as gracefully as possible
		}
	}
	
	
	
	
	public void enable() {
		try {
			
		} catch (Throwable t) {
			//TODO: send to logger and mark module as fatal-errored
		}
	}
	
	public void disable() {
		try {
			
		} catch (Throwable t) {
			//TODO: send to logger and mark module as fatal-errored
		}
	}
	
	/********
	 * IMPLEMENTATION FOR Module
	 ********/
	
	@Override
	@Nonnull
	public String getId() {
		return id;
	}
	
	@Override
	@Nullable
	public String getName() {
		return name;
	}
	
	@Override
	@Nonnull
	public String[] getContributors() {
		if (authors==null) authors = new String[0];
		return authors;
	}

	@Override
	@Nonnull
	public EnableState getState() {
		return state;
	}

	@Override
	@Nullable
	public Module getModule() {
		return module;
	}
}
