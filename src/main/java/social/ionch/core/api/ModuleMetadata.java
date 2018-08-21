package social.ionch.core.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModuleMetadata {
	/**
	 * Gets the unique id for this module.
	 */
	@Nonnull
	String getId();
	
	/**
	 * Gets a friendly name for this module, as specified by the module.jkson metadata. Returns null if there was no name
	 * specified.
	 */
	@Nullable
	String getName();
	
	/**
	 * Gets a list of people that contributed to this module's development. Can't return null, but may return an empty list for
	 * anonymous developers.
	 */
	@Nonnull
	String[] getContributors();
	
	/**
	 * Modules have very few states in their lifecycle, having (hopefully) few interdependencies. This method
	 * returns ENABLED if the mod is enabled, and the module should generally not be touched in any other state.
	 */
	EnableState getState();
	
	/**
	 * Gets the main module class for the module. May return null if EnableState is not ENABLED.
	 */
	@Nullable
	Module getModule();
}
