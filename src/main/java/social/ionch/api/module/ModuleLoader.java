package social.ionch.api.module;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModuleLoader {
	
	/** Returns the id's for all available modules, including ones not currently enabled */
	@Nonnull
	public Set<String> getAvailableModules();
	
	/** Returns the id's for all enabled modules */
	@Nonnull
	public Set<String> getEnabledModules();
	
	/** Gets the enabled-state of the indicated module. Returns DISABLED if the module doesn't exist. */
	@Nonnull
	public EnableState getModuleState(String id);
	
	/** Gets the indicated module if and only if it's currently enabled. Returns null if the module doesn't exist or isn't enabled. */
	@Nullable
	public Module getModule(String id);
	
	/** Returns true if the indicated module exists and is enabled. Returns false otherwise */
	public boolean isEnabled(String id);
	
	/** Returns true if the indicated module exists and is enabled. Returns false otherwise */
	public boolean isEnabled(Module m);
	
	/** Send a module-specific message. If the module doesn't exist, the message is silently discarded. */
	public void sendMessage(String moduleId, String name, String content);
	
	/** Send a global message. All enabled modules will receive this message. */
	public void sendGlobalMessage(String name, String content);
}
