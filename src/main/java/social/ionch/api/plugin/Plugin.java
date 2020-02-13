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

package social.ionch.api.plugin;

/**
 * Base interface for an ionChannel plugin. Provides a barebones lifecycle.
 */
public interface Plugin {

	/**
	 * Called when this plugin is being enabled. This could be because an admin has chosen to enable
	 * it in the control panel, or because the plugin is enabled in the config and the server is
	 * starting. Perform early initialization and registration here. Be aware of the fact this may
	 * be called during early init, or after the server is completely started.
	 */
	void enable();
	/**
	 * Called when this plugin is being hot-disabled, as an admin has chosen to disable it in the
	 * control panel. Unregister anything that has been registered and tear down any resources here.
	 * @throws UnsupportedOperationException if this plugin {@link #canHotDisable cannot be hot-disabled}
	 */
	void hotDisable() throws UnsupportedOperationException;
	
	/**
	 * Some plugins, for some reason, may not be hot-disabled. If this is the case, you may return
	 * false from this method.
	 * @return {@code true} if this plugin can be hot-disabled
	 */
	default boolean canHotDisable() { return true; }
	
	/**
	 * Called when the server is ready for this plugin to perform late-initialization tasks. When
	 * this is called, the database is ready, the server is listening, etc. If the server was
	 * already done starting when this plugin was {@link #enable enabled}, this will be called
	 * immediately.
	 */
	void init();
	/**
	 * Called when the server is performing an orderly shutdown. When this is called, the database,
	 * server, etc, are all still available. Blocking in this method will delay shutdown; do as
	 * little work here as possible. Few plugins will need to perform any cleanup in this method.
	 */
	default void shutdown() {}
	
}
