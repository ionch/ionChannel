package social.ionch.api.activitypub;

import java.util.List;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.SerializedName;
import blue.endless.jankson.annotation.Serializer;

public class Person {
	/** Database-assigned id for this Object */
	protected String databaseId;
	
	/** Canonical URI for looking up this object - derived from database-assigned {@link #databaseId} */
	protected String id;
	
	/** Human-readable name */
	protected String preferredUsername;
	/* All human-readable names. Maybe a bad idea. */
	protected List<String> usernames;
	
	/** Display name */
	protected String name;
	
	@SerializedName("summary")
	protected String bio;
	
	//TODO: Pubkey
	
	@Serializer
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		
		result.put("id", JsonPrimitive.of(id));
		result.put("type", JsonPrimitive.of("Person"));
		
		//These are URLs that depend on the base server installation
		//result.put("following", JsonPrimitive.of("TODO"));
		//result.put("followers", JsonPrimitive.of("TODO"));
		//result.put("inbox",     JsonPrimitive.of("TODO"));
		//result.put("outbox",    JsonPrimitive.of("TODO"));
		
		result.put("preferredUsername", JsonPrimitive.of(preferredUsername));
		result.put("name",    JsonPrimitive.of(name));
		result.put("summary", JsonPrimitive.of(bio));
		result.put("url",     JsonPrimitive.of(id));
		
		JsonArray attachments = new JsonArray();
		result.put("attachment", attachments);
		//TODO: stuff human-readable fields/properties into `attachments`
		
		JsonObject endpoints = new JsonObject();
		endpoints.put("sharedInbox", JsonPrimitive.of("TODO"));
		result.put("endpoints", endpoints);
		
		JsonObject icon = new JsonObject();
		icon.put("type", JsonPrimitive.of("Image"));
		icon.put("mediaType", JsonPrimitive.of("image/png"));
		//icon.put("url", JsonPrimitive.of("TODO")); //This also depends on where server media is kept
		result.put("icon", icon);
		
		return result;
	}
}
