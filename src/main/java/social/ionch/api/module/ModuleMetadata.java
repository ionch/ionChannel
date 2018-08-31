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

package social.ionch.api.module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

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
	
	@Nonnull
	Logger getLogger();
}
