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

package social.ionch.api;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;

public class JsonObjectBuilder {

	private JsonObject object = new JsonObject();

	public JsonObjectBuilder put(String key, String value) {
		return put(key, JsonPrimitive.of(value));
	}

	public JsonObjectBuilder put(String key, long value) {
		return put(key, JsonPrimitive.of(value));
	}

	public JsonObjectBuilder put(String key, double value) {
		return put(key, JsonPrimitive.of(value));
	}

	public JsonObjectBuilder put(String key, boolean value) {
		return put(key, JsonPrimitive.of(value));
	}

	public JsonObjectBuilder put(String key, JsonElement value) {
		return put(key, value, "");
	}
	
	public JsonObjectBuilder put(String key, String value, String comment) {
		return put(key, JsonPrimitive.of(value), comment);
	}

	public JsonObjectBuilder put(String key, long value, String comment) {
		return put(key, JsonPrimitive.of(value), comment);
	}

	public JsonObjectBuilder put(String key, double value, String comment) {
		return put(key, JsonPrimitive.of(value), comment);
	}

	public JsonObjectBuilder put(String key, boolean value, String comment) {
		return put(key, JsonPrimitive.of(value), comment);
	}

	public JsonObjectBuilder put(String key, JsonElement value, String comment) {
		object.put(key, value, comment);
		return this;
	}

	public JsonObject build() {
		return object;
	}

	public String toJson() {
		return object.toJson(JsonGrammar.STRICT);
	}

}
