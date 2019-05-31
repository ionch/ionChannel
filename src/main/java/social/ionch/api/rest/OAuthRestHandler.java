package social.ionch.api.rest;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class OAuthRestHandler implements RestHandler {

	@Override
	public boolean matches(String resourceName) {
		return resourceName.startsWith("/oauth");
	}

	@Override
	public Response handle(String uri, Request request) {
		
		//TODO: OAuth
		
		return null;
	}

}
