package social.ionch.builtin.db;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import social.ionch.api.Ionch;
import social.ionch.api.module.Module;

public class DatabaseModule implements Module {

	@Override
	public ListenableFuture<Void> enable() {
		//Ionch.registerDatabase(db);
		
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
