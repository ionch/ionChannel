package social.ionch.api;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;

public class JsonObjectBuilder {

	private JsonObject object = new JsonObject();

	public JsonObjectBuilder put(String key, String value) {
		return put(key, value);
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
