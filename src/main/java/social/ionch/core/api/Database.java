package social.ionch.core.api;

import javax.annotation.Nullable;

import social.ionch.core.api.impl.DatabaseException;

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
