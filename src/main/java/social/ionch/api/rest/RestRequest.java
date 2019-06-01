package social.ionch.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;

import com.google.common.io.CharStreams;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
import social.ionch.api.social.JsonResourceDescriptor;

/** Represents an *outgoing* rest request. */
public class RestRequest {
	
	protected String url = "";
	protected String accept = "application/activity+json, application/json";
	protected String charset = "utf-8";
	protected boolean forceHttps = false;
	
	protected Future<String> future;
	
	public RestRequest(String uri) {
		this.url = uri;
	}
	
	/** Reccommended for all Requests. Changes the protocol to https and errors if an https connection cannot be negotiated for the resource. */
	public RestRequest requireHttps() {
		forceHttps = true;
		if (!url.startsWith("https://")) {
			//We're going to have to cut off the existing protocol.
			int i = url.indexOf("://");
			if (i!=-1) {
				url = url.substring(i+3);
			}
			url = "https://"+url;
		}
		
		return this;
	}
	
	/**
	 * Sets the MIME type or types which will be accepted for the response. Defaults to "application/activity+json, application/json" for
	 * retrieving ActivityStreams objects (Pleroma dislikes application/ld+json and so do we)
	 * @param type The mime types acceptable for the response, separated by commas, or "* / *" to indicate that any mime type is acceptable.
	 * @return this request for further configuration
	 */
	public RestRequest contentType(String type) {
		this.accept = type;
		return this;
	}
	
	public RestResponse run() {
		try {
			URL u = new URL(url);
			if (forceHttps && !u.getProtocol().equals("https")) return new RestResponse(new IOException("Protocol was not HTTPS."));
			
			URLConnection connection = u.openConnection();
			if (accept!=null) connection.addRequestProperty("Accept", accept);
			connection.addRequestProperty("Accept-Charset", charset);
			
			if (forceHttps && !(connection instanceof HttpsURLConnection)) return new RestResponse(new IOException("Protocol was not HTTPS."));
			
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection c = (HttpURLConnection)connection;
				c.connect();
				
				int code = c.getResponseCode();
				if (code==200) {
					try (InputStream in = c.getInputStream()) {
						String body = CharStreams.toString(new InputStreamReader(in));
						return new RestResponse(code, c.getContentType(), body);
					}
					
				} else {
					return new RestResponse(code, "text/plain", c.getResponseMessage());
				}
				
			} else {
				return new RestResponse(new IOException("Unknown connection type "+connection.getClass().getCanonicalName()));
			}
			
		} catch (IOException e) {
			return new RestResponse(e);
		}
	}
	
	
	//The following are prerolled JRD requests that probably don't belong in this class. But for now they're useful, practical examples
	//TODO: Better server scrubbing, also needed for requireHttps()
	
	@Nonnull
	public static JsonResourceDescriptor webfinger(String server, String id) throws IOException, SyntaxError {
		if (id.startsWith("@")) id = id.substring(1);
		if (server.endsWith("/")) server = server.substring(0, server.length()-1);
		if (server.startsWith("http://")) server = server.substring("http://".length());
		if (server.startsWith("https://")) server = server.substring("https://".length());
		
		RestResponse response = new RestRequest("https://"+server+"/.well-known/webfinger?resource=acct:"+id)
			.contentType("application/json, application/jrd+json")
			.requireHttps()
			.run();
		
		if (response.didError()) throw new IOException(response.getError());
		
		if (response.didRespond()) {
			Jankson jankson = Jankson.builder().build();
			return jankson.fromJson(response.content, JsonResourceDescriptor.class);
		} else {
			throw new IOException("No response from host.");
		}
	}
	
	@Nonnull
	public static JsonResourceDescriptor getNodeInfo(String server) throws IOException, SyntaxError {
		if (server.endsWith("/")) server = server.substring(0, server.length()-1);
		if (server.startsWith("http://")) server = server.substring("http://".length());
		if (server.startsWith("https://")) server = server.substring("https://".length());
		
		RestResponse response = new RestRequest("https://"+server+"/.well-known/nodeinfo")
				.contentType("application/json, application/jrd+json")
				.requireHttps()
				.run();
		
		if (response.didError()) throw new IOException(response.getError());
		
		if (response.didRespond()) {
			Jankson jankson = Jankson.builder().build();
			return jankson.fromJson(response.content, JsonResourceDescriptor.class);
		} else {
			throw new IOException("No response from host.");
		}
	}
}
