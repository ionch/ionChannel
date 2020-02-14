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

package social.ionch.api.db;

import java.util.Map;
import com.google.common.collect.Maps;

import blue.endless.jankson.JsonObject;
import social.ionch.api.ResourcefulReadWriteLock;
import social.ionch.api.ResourcefulReadWriteLock.HeldLock;

public final class DatabaseFactoryRegistry {

	private static final class DatabaseFactoryFacsimile implements DatabaseFactory {
		private final DatabaseFactory delegate;
		public DatabaseFactoryFacsimile(DatabaseFactory delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public String name() {
			return delegate.name();
		}
		
		@Override
		public Database fabricate(JsonObject config) {
			return delegate.fabricate(config);
		}
		
		@Override
		public int hashCode() {
			return 31 * delegate.hashCode();
		}
		
		@Override
		public String toString() {
			return "?<"+delegate.toString()+">";
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (this == o) return true;
			if (o.getClass() != DatabaseFactoryFacsimile.class) return false;
			return ((DatabaseFactoryFacsimile)o).delegate.equals(this.delegate);
		}

	}

	private static final ResourcefulReadWriteLock rwl = ResourcefulReadWriteLock.create();
	private static final Map<String, DatabaseFactory> registry = Maps.newHashMap();
	private static final Map<String, DatabaseFactoryFacsimile> facsimilies = Maps.newHashMap();
	
	/**
	 * Retrieve a facsimile of the DatabaseFactory with the given name. The facsimile opaquely
	 * wraps the implementation, preventing casting or undue unregistration.
	 * @param name the name to look up
	 * @throws IllegalArgumentException if there is no factory registered with the given name
	 * @return a facsimile of the DatabaseFactory with the given name
	 */
	public static DatabaseFactory get(String name) {
		if (name == null) throw new IllegalArgumentException("Cannot get null");
		try (HeldLock hl = rwl.obtainReadLock()) {
			if (!registry.containsKey(name))
				throw new IllegalArgumentException("No database factory with name \""+name+"\" has been registered");
			return facsimilies.get(name);
		}
	}
	
	/**
	 * Register a DatabaseFactory.
	 * @param df the DatabaseFactory to register
	 */
	public static void register(DatabaseFactory df) {
		if (df == null) throw new IllegalArgumentException("Cannot register null");
		String name = df.name();
		try (HeldLock hl = rwl.obtainWriteLock()) {
			if (registry.containsKey(name))
				throw new IllegalStateException("A database factory with name \""+name+"\" is already registered");
			registry.put(name, df);
			facsimilies.put(name, new DatabaseFactoryFacsimile(df));
		}
	}
	
	/**
	 * Unregister a DatabaseFactory. The passed DatabaseFactory must be the original registered
	 * DatabaseFactory, not a facsimile returned by {@link #get} or another DatabaseFactory with the
	 * same name.
	 * @param df the DatabaseFactory to unregister
	 */
	public static void unregister(DatabaseFactory df) {
		if (df == null) throw new IllegalArgumentException("Cannot unregister null");
		String name = df.name();
		try (HeldLock hl = rwl.obtainWriteLock()) {
			if (!registry.containsKey(name))
				throw new IllegalArgumentException("No database factory with name \""+name+"\" has been registered");
			if (df != registry.get(name)) {
				if (df instanceof DatabaseFactoryFacsimile) {
					throw new IllegalArgumentException("You must hold the original database factory to unregister it, not a facsimile");
				}
				throw new IllegalArgumentException("The passed database factory is not the same object as the registered database factory");
			}
			if (registry.remove(name, df)) {
				facsimilies.remove(name);
			}
		}
	}
	
	private DatabaseFactoryRegistry() {}

}
