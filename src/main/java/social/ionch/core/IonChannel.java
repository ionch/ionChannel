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

package social.ionch.core;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;

import social.ionch.api.module.Module;
import social.ionch.builtin.db.DatabaseModule;

public class IonChannel {
	public static final String NAME = "IonChannel";
	public static final String VERSION = "0.0.1";
	public static final String SLUG = "Radically Inclusive Jean Technology";
	public static final String INSTANCE_NAME = "Prime";
	public static final String SERVER_HEADER = NAME+" "+VERSION+" ("+SLUG+")";
	public static final String CLACKS = "Natalie Nguyen, Shiina Mota, Natalie Nguyen, Shiina Mota";
	
	
	public static void main(String[] args) {
		
		loadBuiltins();
		
		HttpServer server = HttpServer.createSimpleServer();
		ServerConfiguration cfg = server.getServerConfiguration();
		cfg.setHttpServerName(NAME);
		cfg.setHttpServerVersion(VERSION);
		cfg.setName(INSTANCE_NAME);
		cfg.setJmxEnabled(false);
		
		
		server.getServerConfiguration().addHttpHandler( //Example Stuff
			    new HttpHandler() {
			        public void service(Request request, Response response) throws Exception {
			        	String responseBody = "<h1>It works!</h1><p>"+request.getDecodedRequestURI()+"</p>";
			            response.setContentType("text/html");
			            response.setContentLength(responseBody.length());
			            response.addHeader("Server", SERVER_HEADER);
			            if (CLACKS!=null && !CLACKS.isEmpty()) response.addHeader("x-clacks-overhead", CLACKS);
			            response.getWriter().write(responseBody);
			        }
			    },
			    "/");
		
		try {
			server.start();
			
			//Example Stuff
			System.out.println("Press the enter key to stop the server.");
			System.in.read();
			
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void loadBuiltins() {
		//TODO: THIS IS AN UNWISE TEMPORARY IMPL which loads builtins on the same classloader as the server and forgets about them. They can't fully, truly be disabled like this.
		
		DatabaseModule db = new DatabaseModule();
		db.enable();
	}
}
