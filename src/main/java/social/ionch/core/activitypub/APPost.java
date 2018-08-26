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

public class APPost {
	/** The URI for this post */
	private String id;
	/** Always "Note" for regular posts. This field appears slightly freeform. */
	private String type = "Note";
	/** full URI for the User that posted this .*/
	private String attributedTo;
	/** BCP47 tag for the content's locale, such as "en" (unspecified English), "zh-hak-CN" (Chinese, Hakka, China), or "tlh" (tlhIngan Hol / Klingon) */
	private String locale;
	/** Original markup system the message was entered in. Default is "text/html", but could be "text/markdown", "text/plain", and so on. */
	private String sourceType = "text/html";
	/** Original markup or rawtext the message was entered in. If interpreted as sourceType, should generate the raw html markup in {@link #content}. Arbitrary content like emoji or shortcodes is allowed. */
	private String sourceContent;
	/** Content of the message, in html. */
	private String content;
	
	
	
	/*
	 * Example:
	 * 
	 *  {
	 *  	"@context": "https://www.w3.org/ns/activitystreams",
	 *  	"type": "Like",
	 *  	"actor": "https://example.net/~mallory",
	 *  	"to": ["https://hatchat.example/sarah/",
	 *  	"https://example.com/peeps/john/"],
	 *  	"object": {
	 *  		"@context": {"@language": "en"},
	 *  		"id": "https://example.org/~alice/note/23",
	 *  		"type": "Note",
	 *  		"attributedTo": "https://example.org/~alice",
	 *  		"content": "I'm a goat"
	 *  	}
	 *  }
	 *  
	 *  We see here an Object wrapped in an Activity: @mallory@example.net "Liked" a post by @alice@example.org, and tagged @sarah@hatchat.example and @john@example.com to notify them.
	 *  The post is, simply, "I'm a goat". This sounds very Mastodon, so I think it's a good example all around.
	 *  
	 *  We also need to keep in mind that the object *does not need to be wrapped* in an Activity. This Note is a resource that can be requested bare at https://example.org/~alice/note/23
	 *  
	 *  Another important thing to process is that instances can and will have different structures for their userids. We can't even assume an https:// scheme for users; I've seen a lot
	 *  of talk about using webfinger-style "acct:mallory@example.net" addresses, so everyone in the activityverse needs to be prepared to follow a URI to discover the Actor involved.
	 */
}
