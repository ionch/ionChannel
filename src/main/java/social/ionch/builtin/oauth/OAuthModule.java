package social.ionch.builtin.oauth;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import social.ionch.api.Ionch;
import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;
import social.ionch.api.rest.OAuthRestHandler;

/**
 * OAuth2 endpoints to allow client authentication per RFC 6749 and https://oauth.net/2/
 * This protocol lays claim to the "/token" and "/auth" URL paths.
 */
public class OAuthModule implements Module {

	@Override
	public ListenableFuture<Void> enable(ModuleMetadata metadata) {
		Ionch.registerRestHandler(new OAuthRestHandler());
		
		return Futures.immediateFuture(null);
	}

	@Override
	public ListenableFuture<Void> disable() {
		return Futures.immediateFuture(null);
	}

	@Override
	public void forceStop() {
		
	}

}
