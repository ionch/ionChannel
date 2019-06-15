package social.ionch.builtin.mastodon;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;

/**
 * The "Mastodon API" is a set of REST endpoints expected to reside at `/api/v1/*`. These in turn
 * get called by the Mastodon or MastoFE web clients or by real apps like Tusky or Mast.
 * 
 * <p>Apps that require this functionality may not be able to function without also discovering OAuth pages.
 */
public class MastodonModule implements Module {

	@Override
	public ListenableFuture<Void> enable(ModuleMetadata metadata) {
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
