package social.ionch.api.rest;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

public class OAuthRestHandler implements RestHandler {

	@Override
	public boolean matches(String resourceName) {
		System.out.println(resourceName);
		return resourceName.startsWith("/oauth");
	}

	@Override
	public boolean handle(String uri, Request request, Response response) {
		
		//TODO: OAuth
		response.setStatus(HttpStatus.NOT_IMPLEMENTED_501);
		
		return true;
	}

}
