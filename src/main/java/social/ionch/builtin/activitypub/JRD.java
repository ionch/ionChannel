package social.ionch.builtin.activitypub;

import java.util.List;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * JRD (Json [extensible] Resource Descriptor) objects are better known as "Webfinger response objects". They
 * contain meta-information about a host or, in the ActivityPub/Webfinger case, one of the host's users.
 * 
 * <p> RFC 7033 describes the process to obtain this object. RFC 6415 Appendix A describes this object itself.
 * But badly, in relation to the XRD spec.
 * 
 * <p> The query for this object typically looks like "https://example.com/.well-known/webfinger?resource=acct:exampleuser@example.com".
 * Ideally, your headers would include "Accept=application/jrd+json" because that sure as heck is what you're going to get.
 */
public class JRD {
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
	
	
	
	public static class Link {
		public String rel = "";
		public String type = "text/html";
		public String href = "";
		public transient String template = "";
		
		//The following are not usually seen in ActivityPub Webfinger results
		public transient HashMap<String, String> titles = new HashMap<>(); //keys are either "default" or ISO 6931 strings
		public transient HashMap<String, String> properties = new HashMap<>();
	}
	
}
