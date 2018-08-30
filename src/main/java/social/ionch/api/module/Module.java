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

package social.ionch.api.module;

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
	
	/**
	 * Receives an inter-process message to this module. If global is true, this message was broadcast to all
	 * modules, not aimed at a particular one. It's highly encouraged to use global messages so that other
	 * modules have an opportunity to respond instead.
	 */
	default void receiveMessage(String name, String contents, boolean global) {
		//By default, do nothing.
	}
}
