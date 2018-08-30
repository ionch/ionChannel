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

package social.ionch.api.db;

import javax.annotation.Nullable;

import social.ionch.api.social.User;

public interface Database {
	/** Looks up a user from the specified id. May return null if no user exists for this id. */
	@Nullable
	public User getUserById(int id) throws DatabaseException;
	
	/** Looks up a user from the specified username. May return null if no user exists for this name,
	 *  or if the user changed their name and there is no forwarding key.
	 *  @param name the "user" part of "@user@example.com" - the semi-permanent human-readable user identifier
	 *  @see {@link User#getName()}
	 */
	@Nullable
	public User getUserByName(String name) throws DatabaseException;
}
