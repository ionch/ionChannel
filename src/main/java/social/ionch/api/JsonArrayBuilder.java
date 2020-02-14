package social.ionch.api;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonPrimitive;

public class JsonArrayBuilder {

	private JsonArray array = new JsonArray();

	public JsonArrayBuilder add(String value) {
		return add(JsonPrimitive.of(value));
	}

	public JsonArrayBuilder add(long value) {
		return add(JsonPrimitive.of(value));
	}

	public JsonArrayBuilder add(double value) {
		return add(JsonPrimitive.of(value));
	}

	public JsonArrayBuilder add(boolean value) {
		return add(JsonPrimitive.of(value));
	}

	public JsonArrayBuilder add(JsonElement value) {
		return add(value, "");
	}
	
	public JsonArrayBuilder add(String value, String comment) {
		return add(JsonPrimitive.of(value), comment);
	}

	public JsonArrayBuilder add(long value, String comment) {
		return add(JsonPrimitive.of(value), comment);
	}

	public JsonArrayBuilder add(double value, String comment) {
		return add(JsonPrimitive.of(value), comment);
	}

	public JsonArrayBuilder add(boolean value, String comment) {
		return add(JsonPrimitive.of(value), comment);
	}

	public JsonArrayBuilder add(JsonElement value, String comment) {
		array.add(value, comment);
		return this;
	}

	public JsonArray build() {
		return array;
	}

	public String toJson() {
		return array.toJson(JsonGrammar.STRICT);
	}

}
