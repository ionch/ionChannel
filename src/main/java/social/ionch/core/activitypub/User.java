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

package social.ionch.core.activitypub;

import java.util.UUID;

public class User implements ActivityStreams.Attribution {
	private int id;
	private String name;
	private String displayName;
	
	/** This is an identifier for this user which is guaranteed to be unique and persistent, but isn't very human-readable. */
	public int getId() {
		return id;
	}
	
	/** 
	 * This is a human-readable identifier which is guaranteed to be unique but not persistent; this is the "user" in "@user@example.com".
	 * 
	 * <p>This field is allowed at our end to contain arbitrary non-alphanumerics, but users are reccommended to choose names which are
	 * comfortable for other people on their instance to type. Markup MUST be escaped-out when displaying this field to users. That is,
	 * if a user is "@&lt;p&gt;@example.com", the '&amp;lt;' and '&amp;gt;' entities must be used to display it on a webpage.
	 * 
	 * <p>This field is likely to be length-limited to around 16-32 characters.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This is the name displayed at the top of any posts by the user. It may contain arbitrary non-alphanumerics, emoji, shortcodes, and
	 * potentially, limited markup. It may be length-limited.
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	
	
	
	/*
	 * items not expressed here, which will likely be generated synthetically in toJson() to conform to ActivityStreams:
	 * 
	 * - inbox             (required)
	 * - outbox            (required)
	 * - following         (required)
	 * - followers         (required)
	 * - liked             (optional)
	 * - streams           (optional)
	 * - preferredUsername (optional) (no uniqueness guarantee)
	 * - endpoints         (optional)
	 * 
	 * The full example Person follows:
	 * 
	 *  {
  	 *  	"@context": ["https://www.w3.org/ns/activitystreams", {"@language": "ja"}],
	 *  	"type": "Person",
	 *  	"id": "https://kenzoishii.example.com/",
	 *  	"following": "https://kenzoishii.example.com/following.json",
	 *  	"followers": "https://kenzoishii.example.com/followers.json",
	 *  	"liked": "https://kenzoishii.example.com/liked.json",
	 *  	"inbox": "https://kenzoishii.example.com/inbox.json",
	 *  	"outbox": "https://kenzoishii.example.com/feed.json",
	 *  	"preferredUsername": "kenzoishii",
	 *  	"name": "石井健蔵",
	 *  	"summary": "この方はただの例です",
	 *  	"icon": [
	 *  		"https://kenzoishii.example.com/image/165987aklre4"
	 *  	]
	 *  }
	 * 
	 * We'll likely be generating and responding to addresses resembling the following:
	 *  {
	 *  	"id": "https://ionch.example.com/@kenzoishii",
	 *  	"following": "https://ionch.example.com/@kenzoishii/following.json"
	 *  	"followers": "https://ionch.example.com/@kenzoishii/followers.json"
	 *  	(...)
	 *  }
	 */
}
