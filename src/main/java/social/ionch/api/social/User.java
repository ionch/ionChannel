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

package social.ionch.api.social;

/**
 * Represents a person, bot, or other "actor" type which has its own user-profile. Could be local or remote.
 * 
 * <p>When using User objects, please be aware that the instant you "check out" a user from the Database, it goes stale. It represents the
 * object's state at the moment of checkout, or in the case of Activity-embedded users, the moment the Activity was Created.
 */
public class User {
	/** Globally-unique URI which represents this user. In practice, an https URL this object can be requested from. */
	protected String id;
	/** The user's internal, official ID. Can be changed, and if you think otherwise, fight me. */
	protected String username;
	/** The user's human-readable name. In ActivityPub this could be a map of language-tagged names, but in practice this is not the case. */
	protected String displayName;
	/** The ActivityPub "core Actor type" of this object. This value may not describe the whole type of the object, but it must accurately reflect the general category. By default Ionch only assigns profiles to Person objects. */
	protected Type type = Type.PERSON;
	
	
	
	/** Gets the globally-unique ActivityPub URI (anyURI) which represents this user. In practice, this is generally an https URL where this object can be requested from. */
	public String getId() {
		return id;
	}
	
	/** Gets the semi-persistent human-readable name of this User, such as the "user" in "@user@instance.town". This matches up to the "preferredUsername" key */
	public String getUsername() {
		return username;
	}
	
	/** Gets the ephemeral display-name for this User, which may contain non-alphanumerics, emoji, and custom emoji shortcodes. IF SENT OVER ACTIVITYPUB, STRIP ALL HTML but not the shortcodes, because Mastodon pretends shortcodes aren't markup. */
	public String getDisplayName() {
		return displayName;
	}
	
	public static enum Type {
		PERSON("Person"),
		APPLICATION("Application"),
		GROUP("Group"),
		ORGANIZATION("Organization"),
		SERVICE("Service");
		
		private final String value;
		
		Type(String stringValue) {
			this.value = stringValue;
		}
		
		public String toString() {
			return value;
		}
	}
}
