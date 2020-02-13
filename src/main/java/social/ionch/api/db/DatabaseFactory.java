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

import java.util.function.Function;
import blue.endless.jankson.JsonObject;

/**
 * Handles creating {@link Database}s from configuration.
 */
public interface DatabaseFactory {
	/**
	 * @return the string that identifies this factory in the config, such as "h2"
	 */
	String name();
	/**
	 * @return a newly constructed Database instance that points to the database represented by the
	 * 		given configuration
	 */
	Database fabricate(JsonObject config);
	
	static DatabaseFactory from(String name, Function<JsonObject, Database> func) {
		return new DatabaseFactory() {
			@Override
			public String name() {
				return name;
			}
			@Override
			public Database fabricate(JsonObject config) {
				return func.apply(config);
			}
			@Override
			public String toString() {
				return "DatabaseFactory.from(\""+name+"\", /*"+func.getClass().getName()+"*/(cfg) -> { ... })";
			}
		};
	}
	
}
