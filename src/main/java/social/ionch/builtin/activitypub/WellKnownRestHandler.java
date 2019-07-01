package social.ionch.builtin.activitypub;

import java.io.IOException;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import social.ionch.api.rest.RestHandler;
import social.ionch.core.IonChannel;

public class WellKnownRestHandler implements RestHandler {

	@Override
	public boolean matches(String resourceName) {
		return resourceName.startsWith("/.well-known/");
	}

	@Override
	public boolean handle(String resourceName, Request request, Response response) {
		if (resourceName.equals("/.well-known/webfinger")) {
			String acctParam = request.getParameter("resource");
			if (acctParam==null) {
				response.setStatus(HttpStatus.BAD_REQUEST_400);
				response.setDetailMessage("Bad webfinger request: No resource specified.");
				return true;
			}
			
			String username = acctParam;
			if (username.startsWith("acct:")) {
				username = username.substring("acct:".length());
				String suffix = "@"+IonChannel.SERVER_SETTINGS.server_name;
				if (username.endsWith(suffix)) {
					username = username.substring(0, username.lastIndexOf(suffix));
				}
				System.out.println("Supplying JRD for user '"+username+"'.");
				
				//TODO: Lookup user
				
				JRD userDescriptor = new JRD();
				String baseAddress = IonChannel.SERVER_SETTINGS.server_name;
				String userid = "https://"+baseAddress+"/users/"+acctParam;
				
				userDescriptor.subject = "acct:"+username+suffix;
				
				
				JRD.Link self = new JRD.Link();
				self.href = userid;
				self.rel = "self";
				self.type = "application/activity+json";
				
				userDescriptor.links.add(self);
				
				String selfString = Jankson.builder().build().toJson(userDescriptor).toJson(JsonGrammar.STRICT);
				response.setContentType("application/jrd+json");
				try {
					response.getWriter().write(selfString);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			} else {
				response.setStatus(HttpStatus.NOT_FOUND_404);
				response.setDetailMessage("The requested resource was unknown to this server.");
				return true;
			}
		} else if (resourceName.equals("./well-known/nodeinfo")) {
			
			
			
			//TODO: Return JRD document
			
			return true;
		} else {
			return false;
		}
	}

}
