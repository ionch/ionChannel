package social.ionch.core.api;

import com.google.common.util.concurrent.ListenableFuture;

public interface Module {
	/**
	 * Acquire any resources needed for operation, register event handlers, etc. Most modules only have basic
	 * setup needs which are complete by the time this method returns, so it's entirely appropriate to return
	 * an already-complete future.
	 */
	ListenableFuture<Void> enable();
	
	/**
	 * Gracefully stop operation. Once operation has finally stopped, complete the future you provide, so that
	 * ionch can unload resources related to this module.
	 */
	ListenableFuture<Void> disable();
	
	/**
	 * Immediately and unconditionally release resources and halt module behavior. After ionch calls this and
	 * it returns, the plug will be pulled on this module, and things will stop working! Specifically, at the
	 * discretion of the host, threads may be stopped and plugin-specific classes may be forcibly unloaded.
	 * (no cheating: never return, and the watchdog thread will get angery)
	 * 
	 * <p>Kind of like "kill -9", this only happens as a last resort, if disable is unable to gracefully unload
	 * the module and the administrator gets impatient. DO NOT CALL unless you're absolutely certain you want
	 * to nuke it from orbit.
	 */
	void forceStop();
}
