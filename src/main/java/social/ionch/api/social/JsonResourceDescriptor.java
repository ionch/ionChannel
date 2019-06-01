package social.ionch.api.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

/** An object describing a webfinger or nodeinfo response.
 * 
 * <p>See RFC 7033 for the webfinger process, and RFC 6415 Appendix A for the spec of this object itself.
 * Fair warning: It's an adaptation of the XRD spec, so its description is not at all clear.
 * 
 * <p>The query for this object typically looks something like "https://example.com/.well-known/webfinger?resource=acct:exampleuser@example.com".
 * The response contentType should be expected to be "application/jrd+json", and it is safe to specify this in the "Accept" header.
 * 
 * <p>The structure of this POJO is rigged so that Jankson's deserializer can pack and unpack this with no custom code.
 */
public class JsonResourceDescriptor {
	public String subject;
	
	public List<String> aliases = new ArrayList<>();
	
	/**
	 * These can be filtered by adding a "rel=X" query parameter to the HTTP request
	 */
	public List<Link> links = new ArrayList<>();
	
	
	//The following are not usually seen in ActivityPub Webfinger results
	@Nullable
	public String expires = null;
	@Nullable
	public HashMap<String, String> properties = null;
	
	
	
	@Nullable
	public Link getLink(String rel) {
		for(Link link: links) {
			if (rel.equals(link.rel)) return link;
		}
		return null;
	}
	
	/** Probably the most common use for JRD entries is this: resource discovery.
	 * 
	 * <p>Common rels found thusly are:
	 * <ul>
	 *   <li>"http://webfinger.net/rel/profile-page" (text/html): Human-readable profile
	 *   <li>"http://schemas.google.com/g/2010#updates-from" (application/atom+xml): Exposed for users by most mastodon instances, provides their public timeline as an atom feed.
	 *   <li>"self" (application/activity+json): ActivityPub user profile. Note: If you don't specify application/json, application/ld+json, or application/activity+json, you'll usually get text/html instead.
	 *   <li>"salmon" (application/activity+json): OStatus replies, but ActivityPub uses the user's inbox or server sharedInbox instead of this endpoint
	 *   <li>"magic-public-key" (text/html): data URI directly encoding an RSA key. Again, this is for OStatus, you encode a message to a user using their pubkey so they can decode it with their private one.
	 *   <li>"http://ostatus.org/schema/1.0/subscribe" (text/html): A template-based URL used for authorizing OStatus interaction (a friend request page).
	 * </ul>
	 */
	@Nullable
	public String getLinkHref(String rel) {
		Link link = getLink(rel);
		
		if (link.href!=null && !link.href.isEmpty()) return link.href;
		//TODO: deal with templates?
		return null;
	}
	
	
	public static class Link {
		public String rel = "";
		public String type = "text/html";
		public String href = "";
		public String template = "";
		
		//The following are not usually seen in ActivityPub Webfinger results
		public HashMap<String, String> titles = new HashMap<>(); //keys are either "default" or ISO 6931 strings
		public HashMap<String, String> properties = new HashMap<>();
	}
}
