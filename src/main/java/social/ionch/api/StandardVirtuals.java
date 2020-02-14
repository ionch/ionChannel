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

package social.ionch.api;

import social.ionch.api.plugin.Plugin;

/**
 * Standard virtual plugin IDs for use in {@link Plugin#provides} and related methods.
 */
public final class StandardVirtuals {

	/**
	 * Provided by plugins that contain a database implementation.
	 */
	public static final String DATABASE = "?social.ionch.db";
	
	private StandardVirtuals() {}
	
}
