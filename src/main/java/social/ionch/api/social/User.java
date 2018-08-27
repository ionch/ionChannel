package social.ionch.api.social;

/**
 * Represents a person, bot, or other "actor" type which has its own user-profile. A concrete convenience implementation is available
 * at {@link social.ionch.core.impl.SimpleUser}
 * 
 * <p>This class and its concrete implementations may be implemented or subclassed, with the following caveats:
 * <ul>
 *   <li>Any subclass MUST implement {@link #equals(Object)} and {@link #hashCode()}
 *   <li>Any added fields which are not part of this interface MAY be silently ignored by any Module including Database serializers.
 * </ul>
 */
public interface User {
	/** Gets the persistent, server-unique key that identifies this User. */
	public int getId();
	
	/** Gets the semi-persistent human-readable name of this User, such as the "user" in "@user@instance.town" */
	public String getName();
	
	/** Gets the ephemeral display-name for this User, which may contain non-alphanumerics, emoji, custom emoji shortcodes, and html markup */
	public String getDisplayName();
}
