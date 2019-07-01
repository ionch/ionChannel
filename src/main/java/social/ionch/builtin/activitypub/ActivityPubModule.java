package social.ionch.builtin.activitypub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import social.ionch.api.Ionch;
import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;

/**
 * The heart and soul of ion channel, the ActivityPub functionality. This includes static (templated) pages for
 * content, hooks to trigger salmon pushes, and REST endpoints for inboxes, outboxes, and salmon traffic.
 * 
 * <p>As a matter of convenience, this also sets up some /.well-known endpoints for webfinger and nodeinfo.
 */
public class ActivityPubModule implements Module {

	@Override
	public ListenableFuture<Void> enable(ModuleMetadata metadata) {
		Ionch.registerRestHandler(new WellKnownRestHandler());
		
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
