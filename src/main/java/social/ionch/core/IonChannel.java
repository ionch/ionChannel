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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import social.ionch.api.Ionch;
import social.ionch.builtin.activitypub.ActivityPubModule;
import social.ionch.builtin.db.DatabaseModule;
import social.ionch.builtin.mastodon.MastodonModule;
import social.ionch.builtin.oauth.OAuthModule;
import social.ionch.core.module.BaseModuleLoader;

public class IonChannel {
	public static final String NAME = "IonChannel";
	public static final String VERSION = "0.0.1";
	public static final String SLUG = "Radically Inclusive Jean Technology";
	public static final String INSTANCE_NAME = "Prime";
	public static final String SERVER_HEADER = NAME+" "+VERSION+" ("+SLUG+")";
	public static final String CLACKS = "Natalie Nguyen, Shiina Mota, Natalie Nguyen, Shiina Mota";
	
	public static final Logger LOG = LoggerFactory.getLogger("IonChannel");
	
	private static BaseModuleLoader LOADER = new BaseModuleLoader();
	
	public static void main(String[] args) {
		
		Ionch.registerModuleLoader(LOADER);
		ListenableFuture<Void> enabled = loadBuiltins();
		
		enabled.addListener(()->{
			System.out.println("module_status {");
			for(String s : LOADER.getAvailableModules()) {
				System.out.println("    "+s+": "+LOADER.getModuleState(s));
			}
			System.out.println("}");
			
			
			
			startServer();
			
		}, MoreExecutors.directExecutor());
		
		
	}
	
	
	
	public static void startServer() {
		disableSslVerification();
		
		HttpServer server = HttpServer.createSimpleServer();
		ServerConfiguration cfg = server.getServerConfiguration();
		cfg.setHttpServerName(NAME);
		cfg.setHttpServerVersion(VERSION);
		cfg.setName(INSTANCE_NAME);
		cfg.setJmxEnabled(false);
		
		server.getServerConfiguration().addHttpHandler( //Example Stuff
			    new HttpHandler() {
			        public void service(Request request, Response response) throws Exception {
			        	if (CLACKS!=null && !CLACKS.isEmpty()) response.addHeader("x-clacks-overhead", CLACKS);
			        	
			        	
			        	//Todo: Match against REST endpoint handlers
			        	System.out.println(""+request.getProtocol() + " " + request.getMethod() + " "+ request.getServerName()+":"+request.getServerPort()+" "+request.getRequestURI());
			        	System.out.println("headers: {");
			        	for(String s : request.getHeaderNames()) {
			        		List<String> values = ImmutableList.copyOf(request.getHeaders(s));
			        		System.out.println("    "+s+": "+values.toString());
			        	}
			        	System.out.println("}");
			        	System.out.println("Content-Type: "+request.getContentType());
			        	System.out.println();
			        	System.out.println("body: {");
			        	String body = CharStreams.toString(new InputStreamReader(request.getInputStream()));
			        	System.out.println(body);
			        	System.out.println("}");
			        	
			        	
			        	
			        	
			        	boolean handled = Ionch.handle(request, response);
			        	if (!handled) {
			        		response.setStatus(HttpStatus.NOT_FOUND_404);
			        		response.setError();
			        	}
			        	
			        	/*
			        	String responseBody = "<h1>It works!</h1><p>"+request.getDecodedRequestURI()+"</p>";
			            response.setContentType("text/html");
			            response.setContentLength(responseBody.length());
			            response.addHeader("Server", SERVER_HEADER);
			            if (CLACKS!=null && !CLACKS.isEmpty()) response.addHeader("x-clacks-overhead", CLACKS);
			            response.getWriter().write(responseBody);*/
			        }
			    },
			    "/");
		
		try {
			server.start();
			
			//Example Stuff
			LOG.info("Press the enter key to stop the server.");
			System.in.read();
			
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static ListenableFuture<Void> loadBuiltins() {
		//TODO: THIS IS AN UNWISE TEMPORARY IMPL which loads builtins on the same classloader as the server and forgets about them. They can't fully, truly be disabled like this.
		
		DatabaseModule db = new DatabaseModule();
		LOADER.constructBuiltin(db, "{id:'database'}");
		
		ActivityPubModule ap = new ActivityPubModule();
		LOADER.constructBuiltin(ap, "{id:'activitypub'}");
		
		MastodonModule mo = new MastodonModule();
		LOADER.constructBuiltin(mo, "{id:'mastodon'}");
		
		OAuthModule oa = new OAuthModule();
		LOADER.constructBuiltin(oa, "{id:'oauth2'}");
		
		ListenableFuture<Void> enabled = LOADER.enableAll(); //Later we'll pull enabled/disabled settings from a config so you can prevent builtins from loading. For now just enable everything we know about.
		//db.enable();
		return enabled;
		
		//TODO: set a listener for when loader finds all modules loaded - some kind of future chain or shared future
	}
	
	@Nullable
	public static String retrieve(String urlString, @Nullable String postBody, @Nullable String contentType) {
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			
			if (contentType!=null) {
				connection.addRequestProperty("Accept", contentType);
			}
			
			if (connection instanceof HttpURLConnection) {
				
				HttpURLConnection c = (HttpURLConnection) connection;
				if (postBody!=null) {
					c.setRequestMethod("POST");
					c.setDoOutput(true);
				}
				c.connect();
				
				//if (connection instanceof HttpsURLConnection) {
				//	Certificate[] certificates = new Certificate[0];
				//	certificates = ((HttpsURLConnection)connection).getServerCertificates();
				//	System.out.println("Certificates: "+Arrays.toString(certificates));
				//}
				
				if (postBody!=null) {
					c.getOutputStream().write(postBody.getBytes(StandardCharsets.UTF_8));
				}
				
				int code = c.getResponseCode();
				if (code==200) {
					System.out.println("Receiving "+c.getContentType());
					try (InputStream in = c.getInputStream()) {
						return CharStreams.toString(new InputStreamReader(in));
					}
					
				} else {
					System.out.println("HTTP "+code+" "+c.getResponseMessage());
					return null;
				}
			} else {
				System.out.println("Wrong connection type.");
				return null;
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Note: NEVER, EVER DO THIS.
	 */
	public static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
}
