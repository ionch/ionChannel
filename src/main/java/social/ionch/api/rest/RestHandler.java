package social.ionch.api.rest;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public interface RestHandler {
	/**
	 * Returns whether this request should be chosen to handle a request for the given resource.
	 * @param resourceName the "path" portion of the request, without query parameters
	 * @return true if this handler is applicable to the request and should run.
	 */
	public boolean matches(String resourceName);
	
	//TODO: Reduce API leakage of 'grizzly' details
	/**
	 * Creates the Response for the request. <em>THIS METHOD IS SUBJECT TO CHANGE</em>
	 * @param resourceName the "path" portion of the request, without query parameters
	 * @param request the full Grizzly Request that came in.
	 * @return a Response that should be passed back to the user
	 */
	public Response handle(String resourceName, Request request);
}
