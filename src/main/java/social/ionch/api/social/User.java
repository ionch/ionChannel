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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Represents a person, bot, or other "actor" type which has its own user-profile. Could be local or remote.
 * 
 * <p>When using User objects, please be aware that the instant you "check out" a user from the Database or a foreign server, it goes stale. It represents the
 * object's state at the moment of checkout, or in the case of Activity-embedded users, the moment the Activity was Created.
 */
public class User {
	/** Globally-unique URI which represents this user. In practice, an https URL this object can be requested from. */
	protected String id;
	/** The user's internal, official ID. Can be changed, and if you think otherwise, fight me. */
	protected String preferredUsername;
	/** The user's human-readable name. In ActivityPub this could be a map of language-tagged names, but in practice this is not the case. */
	protected String name;
	/** The ActivityPub "core Actor type" of this object. This value may not describe the whole type of the object, but it must accurately reflect the general category. By default Ionch only assigns profiles to Person objects. */
	protected Type type = Type.Person;
	/** The text that appears on the User's profile page. Html. */
	protected String summary;
	
	/** A URL to an ActivityPub List object containing actors this User is following. */
	protected String following;
	/** A URL to an ActivityPub List object containing actors that have permission to follow this User. */
	protected String followers;
	/** A URL where Activities can be posted to this user to notify them, for instance when an actor they are following posts a status. */
	protected String inbox;
	/** A URL for an OrderedCollection ActivityPub object containing (at least) recent posts from this User, filtered by whatever access level the requester has. */
	protected String outbox;
	/** A URL for a collection. No testing has been done yet, but I suspect this is pinned posts. */
	protected String featured;
	/** The main url for the user's human-readable profile. */
	protected String url;
	/** True if this user has chosen to manually review and accept or decline follow requests. */
	protected boolean manuallyApprovesFollowers;
	/** Attachments to this User's profile - to date this has just been "property fields" the user displays on their human-readable profile page. */
	protected List<AttachmentProperty> attachment = new ArrayList<>();
	/** ActivityPub endpoints related to this user - in practice, contains a "sharedInbox" key for the user's server. */
	protected Map<String, String> endpoints = new HashMap<>();
	
	protected Icon icon;
	protected PublicKey publicKey;
	//TODO: tag list property - empty in practice though.
	
	/** Gets the globally-unique ActivityPub URI (anyURI) which represents this user. In practice, this is generally an https URL where this object can be requested from. */
	public String getId() {
		return id;
	}
	
	/** Gets the semi-persistent human-readable name of this User, such as the "user" in "@user@instance.town". This matches up to the "preferredUsername" key */
	public String getUsername() {
		return preferredUsername;
	}
	
	/** Gets the ephemeral display-name for this User, which may contain non-alphanumerics, emoji, and custom emoji shortcodes. IF SENT OVER ACTIVITYPUB, STRIP ALL HTML but not the shortcodes, because Mastodon pretends shortcodes aren't markup. */
	public String getDisplayName() {
		return name;
	}
	
	@Nullable
	public String getIconUrl() {
		return (icon==null) ? null : icon.url;
	}
	
	public static class AttachmentProperty {
		public String type = "PropertyValue";
		public String name;
		public String value;
	}
	
	public static class Icon {
		public String type = "Image";
		public String mediaType = "image/png";
		public String url;
	}
	
	public static class PublicKey {
		/** Usually url#main-key */
		public String id;
		/** Usually this User's id */
		public String owner;
		/** full public key, including "-----BEGIN PUBLIC KEY-----", newlines, etc */
		public String publicKeyPem;
	}
	
	public static enum Type {
		Person,
		Application,
		Group,
		Organization,
		Service;
	}
}
