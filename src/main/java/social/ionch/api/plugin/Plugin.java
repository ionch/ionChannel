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

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import social.ionch.SkeletonKey;
import social.ionch.builtin.BuiltInPlugin;

/**
 * Base class for an ionChannel plugin. Provides a barebones lifecycle.
 */
public abstract class Plugin {

	private String id;
	private String name;
	private String author;
	private ImmutableSet<String> provides = ImmutableSet.of();
	private ImmutableSet<String> conflicts = ImmutableSet.of();
	private ImmutableSet<String> needs = ImmutableSet.of();
	private ImmutableSet<String> wants = ImmutableSet.of();
	
	private boolean unclean;
	
	/**
	 * Set this plugin's ID. Must be called in the constructor. Should be like a Java package name
	 * to avoid conflicts, such as "social.ionch.h2". Cannot start with a ? - IDs starting with a
	 * question mark are virtual and may only be used with provides.
	 */
	protected final void id(String id) {
		if (this.id != null) throw new IllegalStateException("ID is already set");
		if (id.startsWith("?")) throw new IllegalArgumentException("A plugin's ID cannot be virtual");
		this.id = id;
	}
	/**
	 * Set this plugin's user-friendly name. If not set, defaults to the plugin ID. Can be called in
	 * the constructor.
	 */
	protected final void name(String name) {
		if (this.name != null) throw new IllegalStateException("Name is already set");
		this.name = name;
	}
	/**
	 * Set this plugin's author. Can be called in the constructor.
	 */
	protected final void author(String author) {
		if (this.author != null) throw new IllegalStateException("Author is already set");
		this.author = author;
	}
	/**
	 * Set this plugin's provides list - these are the plugin IDs this plugin should additionally
	 * count as for dependencies. Generally, these should be virtual plugin IDs, which only differ
	 * from normal plugin IDs in that they do not represent any extant plugin. For additional
	 * differentiation, virtual plugin IDs should start with a question mark ("?"). Can be called in
	 * the constructor.
	 */
	protected final void provides(String... provides) {
		if (!this.provides.isEmpty()) throw new IllegalStateException("Provides already set");
		this.provides = ImmutableSet.copyOf(provides);
	}
	/**
	 * Set this plugin's conflicts list - plugin IDs that, if enabled, prevent this plugin from
	 * being enabled. It is legal for a plugin to conflict with a plugin ID that it provides; this
	 * makes the plugin mutually exclusive with other plugins providing the same ID. Can be called
	 * in the constructor.
	 */
	protected final void conflicts(String... conflicts) {
		if (!this.conflicts.isEmpty()) throw new IllegalStateException("Conflicts already set");
		this.conflicts = ImmutableSet.copyOf(conflicts);
	}
	/**
	 * Set this plugin's needs list - plugin IDs that must be enabled for this plugin to be
	 * enabled. Can be called in the constructor.
	 */
	protected final void needs(String... needs) {
		if (!this.needs.isEmpty()) throw new IllegalStateException("Needs already set");
		this.needs = ImmutableSet.copyOf(needs);
	}
	/**
	 * Set this plugin's wants list - plugin IDs that, if present, must be enabled before this
	 * plugin. Can be called in the constructor.
	 */
	protected final void wants(String... wants) {
		if (!this.wants.isEmpty()) throw new IllegalStateException("Wants already set");
		this.wants = ImmutableSet.copyOf(wants);
	}
	
	/**
	 * @return this plugin's ID - should be formatted like a Java package name
	 */
	public final String getId() {
		return id;
	}
	/**
	 * @return this plugin's user-friendly name, or its ID if it doesn't have one
	 */
	public final String getName() {
		return name == null ? id : name;
	}
	/**
	 * @return {@code true} if this plugin has a user-friendly name
	 */
	public final boolean hasName() {
		return name != null;
	}
	/**
	 * @return this plugin's author, or null if not set
	 */
	public @Nullable String getAuthor() {
		return author;
	}
	/**
	 * @return {@code true} if this plugin has an author
	 */
	public final boolean hasAuthor() {
		return author != null;
	}
	/**
	 * @return this plugin's provides list - the plugin IDs this plugin should additionally count as
	 * 		for dependencies
	 */
	public final ImmutableSet<String> getProvides() {
		return provides;
	}
	/**
	 * @return this plugin's conflicts list - plugin IDs that, if enabled, prevent this plugin from
	 * 		being enabled
	 */
	public final ImmutableSet<String> getConflicts() {
		return conflicts;
	}
	/**
	 * @return this plugin's needs list - plugin IDs that must be enabled for this plugin to be
	 * 		enabled
	 */
	public final ImmutableSet<String> getNeeds() {
		return needs;
	}
	/**
	 * @return this plugin's wants list - plugin IDs that, if present, must be enabled before this
	 * 		plugin
	 */
	public final ImmutableSet<String> getWants() {
		return wants;
	}
	
	/**
	 * Called when this plugin is being enabled. This could be because an admin has chosen to enable
	 * it in the control panel, or because the plugin is enabled in the config and the server is
	 * starting. Perform early initialization and registration here. Be aware of the fact this may
	 * be called during early init, or after the server is completely started.
	 */
	public abstract void enable();
	/**
	 * Called when this plugin is being hot-disabled, as an admin has chosen to disable it in the
	 * control panel. Unregister anything that has been registered and tear down any resources here.
	 * @throws UnsupportedOperationException if this plugin {@link #canHotDisable cannot be hot-disabled}
	 */
	public abstract void hotDisable() throws UnsupportedOperationException;
	
	/**
	 * Some plugins, for some reason, may not be hot-disabled. If this is the case, you may return
	 * false from this method.
	 * @return {@code true} if this plugin can be hot-disabled
	 */
	public boolean canHotDisable() { return true; }
	
	/**
	 * Upon plugin load, ionChannel performs a short cleanliness check if {@link #canHotDisable()}
	 * returns true to ensure basic consistency between enable/disable. If this check fails, this
	 * method will return {@code true}.
	 * @return {@code true} if this plugin failed the cleanliness check
	 */
	public final boolean isUnclean() {
		return unclean;
	}
	
	/**
	 * Called when the server is ready for this plugin to perform late-initialization tasks. When
	 * this is called, the database is ready, the server is listening, etc. If the server was
	 * already done starting when this plugin was {@link #enable enabled}, this will be called
	 * immediately.
	 */
	public abstract void init();
	/**
	 * Called when the server is performing an orderly shutdown. When this is called, the database,
	 * server, etc, are all still available. Blocking in this method will delay shutdown; do as
	 * little work here as possible. Few plugins will need to perform any cleanup in this method.
	 */
	public void shutdown() {}
	
	/**
	 * @return a string representing this plugin, in form "Name (ID) by Author", omitting missing
	 * 		information
	 */
	public final String toFriendlyString() {
		String s;
		if (hasName()) {
			s = getName()+" ("+getId()+")";
		} else {
			s = getId();
		}
		if (this instanceof BuiltInPlugin) {
			s = s+" [Built-in]";
		} else if (hasAuthor()) {
			s = s+" by "+getAuthor();
		}
		return s;
	}
	
	/**
	 * Internal use only.
	 */
	public final void $$_markUnclean(SkeletonKey key) {
		SkeletonKey.verify(key);
		unclean = true;
	}
	
}
