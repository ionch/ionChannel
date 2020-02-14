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

package social.ionch.api.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

/**
 * Entry point for contributing sections to and working with the ionch.jkson file. Any settings the
 * admin may want to change while the server is offline should go here instead of into the database.
 */
public class ConfigHandler {

	private static final String FILE_COMMENT =
			"/* This is a Jankson file.\n" +
			"   \n" +
			"   Jankson is a JSON parser that supports a variety of \"quirks\" to make writing JSON files more\n" +
			"   forgiving. The .jkson file extension is used to represent every quirk being enabled. Other common\n" +
			"   file extensions used for Jankson-read files are .json5 and .hjson, to represent different subsets\n" +
			"   of quirks being enabled.\n" +
			"   \n" +
			"   A .jkson file, therefore, differs from a .json file in the following ways:\n" +
			"   - Comments are permitted, and persisted through file rewrites (with some limitations)\n" +
			"   - Commas are always optional\n" +
			"   - Keys do not need to be quoted\n" +
			"   - The braces for the root object may be omitted\n" +
			"   - Infinity, -Infinity, and NaN may be used as bare keywords instead of strings\n" +
			"*/\n\n";
	
	private static final JsonGrammar JKSON = JsonGrammar.builder()
			.bareRootObject(true)
			.bareSpecialNumerics(true)
			.printCommas(false)
			.printUnquotedKeys(true)
			.printWhitespace(true)
			.withComments(true)
			.build();
	private static final ThreadLocal<Jankson> jank = ThreadLocal.withInitial(() -> Jankson.builder().allowBareRootObject().build());
	
	private static final Map<String, JsonObject> contributedSections = Maps.newHashMap();
	private static final Map<String, String> sectionComments = Maps.newHashMap();
	
	private static JsonObject consolidatedCache = null;
	
	public static void contributeSection(String path, JsonObject defaults) {
		contributeSection(path, defaults, "");
	}
	
	public static void contributeSection(String path, JsonObject defaults, String comment) {
		synchronized (contributedSections) {
			if (contributedSections.containsKey(path))
				throw new IllegalStateException("A section with path '"+path+"' has already been contributed");
			contributedSections.put(path, defaults);
			sectionComments.put(path, comment);
			consolidatedCache = null;
		}
	}
	
	public static void removeSection(String path) {
		synchronized (contributedSections) {
			if (!contributedSections.containsKey(path))
				throw new IllegalStateException("A section with path '"+path+"' has not been contributed");
			contributedSections.remove(path);
			consolidatedCache = null;
		}
	}
	
	/**
	 * Combine all the contributed sections and their defaults into one big JsonObject. Caches when
	 * possible.
	 */
	public static JsonObject getConsolidatedDefaults() {
		synchronized (contributedSections) {
			if (consolidatedCache != null) return consolidatedCache;
			JsonObject empty = new JsonObject();
			JsonObject root = new JsonObject();
			for (Map.Entry<String, JsonObject> en : contributedSections.entrySet()) {
				String key = en.getKey();
				String parent = key.contains(".") ? key.substring(key.lastIndexOf('.')+1) : "";
				String basename = key.substring(key.lastIndexOf('.')+1);
				JsonObject parentObj = parent.isEmpty() ? root : root.recursiveGetOrCreate(JsonObject.class, parent, empty, "");
				parentObj.put(basename, en.getValue().clone(), sectionComments.get(en.getKey()));
			}
			consolidatedCache = root;
			return root;
		}
	}
	
	private static JsonObject mergeDefaults(JsonObject obj, JsonObject defaults) {
		JsonObject target = obj.clone();
		for (Map.Entry<String, JsonElement> en : defaults.entrySet()) {
			String comment = defaults.getComment(en.getKey());
			if (target.get(en.getKey()) instanceof JsonObject && en.getValue() instanceof JsonObject) {
				mergeDefaults((JsonObject)target.get(en.getKey()), (JsonObject)en.getValue());
			} else {
				target.put(en.getKey(), en.getValue().clone(), comment);
			}
		}
		return target;
	}
	
	public static void write(File f, JsonObject obj) throws IOException {
		Files.asCharSink(f, Charsets.UTF_8).write(FILE_COMMENT+mergeDefaults(obj, getConsolidatedDefaults()).toJson(JKSON));
	}
	
	public static JsonObject read(File f) throws IOException, SyntaxError {
		return mergeDefaults(jank.get().load(f), getConsolidatedDefaults());
	}

}
