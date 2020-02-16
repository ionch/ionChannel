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

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import social.ionch.SkeletonKey;

/**
 * Entry point for contributing sections to and working with the ionch.jkson file. Any settings the
 * admin may want to change while the server is offline should go here instead of into the database.
 */
public class ConfigSectionHandler {

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
	private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.whitespace());
	private static final String PARAGRAPH_SEPARATOR_MAGIC = "\0$$AStringThatIsUnlikelyToAppearInTheInput$$\0";
	
	private static final Map<String, JsonObject> contributedSections = Maps.newLinkedHashMap();
	private static final Map<String, String> sectionComments = Maps.newHashMap();

	private static Map<String, JsonObject> savedContributedSections = null;
	private static Map<String, String> savedSectionComments = null;
	private static boolean savedStateCopied = false;
	
	private static JsonObject consolidatedCache = null;
	
	public static void contributeSection(String path, JsonObject defaults) {
		contributeSection(path, defaults, "");
	}
	
	public static void contributeSection(String path, JsonObject defaults, String comment) {
		synchronized (contributedSections) {
			if (contributedSections.containsKey(path))
				throw new IllegalStateException("A section with path '"+path+"' has already been contributed");
			copySavedStateIfNeeded();
			int indents = (int)path.chars().filter(i -> i == '.').count();
			JsonObject mangled = defaults.clone();
			mangleComments(indents+1, mangled);
			contributedSections.put(path, mangled);
			comment = mangleComment(indents, comment);
			sectionComments.put(path, comment);
			consolidatedCache = null;
		}
	}
	
	private static void mangleComments(int indents, JsonObject obj) {
		for (String key : obj.keySet()) {
			obj.setComment(key, mangleComment(indents, obj.getComment(key)));
			JsonElement ele = obj.get(key);
			if (ele instanceof JsonObject) {
				mangleComments(indents + 1, (JsonObject)ele);
			} else if (ele instanceof JsonArray) {
				mangleComments(indents + 1, (JsonArray)ele);
			}
		}
	}
	private static void mangleComments(int indents, JsonArray arr) {
		for (int i = 0; i < arr.size(); i++) {
			arr.setComment(i, mangleComment(indents, arr.getComment(i)));
			JsonElement ele = arr.get(i);
			if (ele instanceof JsonObject) {
				mangleComments(indents + 1, (JsonObject)ele);
			} else if (ele instanceof JsonArray) {
				mangleComments(indents + 1, (JsonArray)ele);
			}
		}
	}

	private static String mangleComment(int indents, String comment) {
		if (comment == null) return null;
		if (comment.isBlank()) return comment;
		// 100 columns, minus the 3-space comment indent and 8-space tabs used by most terminals
		int columns = (100 - 3) - (indents*8);
		if (!comment.contains("\n") && comment.length() < columns) return comment;
		// keep paragraph separators
		comment = comment.replace("\n\n", " "+PARAGRAPH_SEPARATOR_MAGIC+" ");
		// remove individual newlines
		comment = comment.replace('\n', ' ');
		comment = wrap(comment, columns);
		return comment;
	}

	private static String wrap(String str, int columns) {
		StringBuilder sb = new StringBuilder();
		int len = 0;
		for (String word : WHITESPACE_SPLITTER.split(str)) {
			if (PARAGRAPH_SEPARATOR_MAGIC.equals(word)) {
				if (len > 0) sb.append("\n");
				len = 0;
				sb.append("\n");
				continue;
			} else if (len + word.length()+1 >= columns) {
				sb.append("\n");
				len = 0;
			} else if (len > 0) {
				sb.append(" ");
				len++;
			}
			len += word.length();
			sb.append(word);
		}
		return sb.toString();
	}

	public static void removeSection(String path) {
		synchronized (contributedSections) {
			if (!contributedSections.containsKey(path))
				throw new IllegalStateException("A section with path '"+path+"' has not been contributed");
			copySavedStateIfNeeded();
			contributedSections.remove(path);
			sectionComments.remove(path);
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
				String parent = key.contains(".") ? key.substring(0, key.lastIndexOf('.')) : "";
				String basename = key.substring(key.lastIndexOf('.')+1);
				JsonObject parentObj = parent.isEmpty() ? root : root.recursiveGetOrCreate(JsonObject.class, parent, empty, "");
				parentObj.put(basename, merge(parentObj.get(basename), en.getValue().clone()), sectionComments.get(en.getKey()));
			}
			consolidatedCache = root;
			return root;
		}
	}
	
	private static JsonElement merge(JsonElement a, JsonElement b) {
		if (a instanceof JsonObject && b instanceof JsonObject) {
			JsonObject out = ((JsonObject)a).clone();
			for (Map.Entry<String, JsonElement> en : ((JsonObject)b).entrySet()) {
				out.put(en.getKey(), merge(out.get(en.getKey()), en.getValue()), ((JsonObject)b).getComment(en.getKey()));
			}
			return out;
		}
		return b;
	}

	private static JsonObject mergeDefaults(JsonObject obj, JsonObject defaults) {
		JsonObject target = obj.clone();
		for (Map.Entry<String, JsonElement> en : defaults.entrySet()) {
			String comment = defaults.getComment(en.getKey());
			if (target.get(en.getKey()) instanceof JsonObject && en.getValue() instanceof JsonObject) {
				target.put(en.getKey(), mergeDefaults((JsonObject)target.get(en.getKey()), (JsonObject)en.getValue()));
			} else if (!target.containsKey(en.getKey())) {
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
	
	private static void copySavedStateIfNeeded() {
		if (savedContributedSections != null && !savedStateCopied) {
			savedContributedSections = Maps.newHashMap(savedContributedSections);
			savedSectionComments = Maps.newHashMap(savedSectionComments);
			savedStateCopied = true;
		}
	}
	
	/**
	 * Internal use only.
	 */
	public static void $$_saveState(SkeletonKey key) {
		SkeletonKey.verify(key);
		synchronized (contributedSections) {
			savedContributedSections = Maps.newHashMap(contributedSections);
			savedSectionComments = Maps.newHashMap(sectionComments);
			savedStateCopied = false;
		}
	}

	/**
	 * Internal use only.
	 */
	public static boolean $$_restoreState(SkeletonKey key) {
		SkeletonKey.verify(key);
		synchronized (contributedSections) {
			boolean rtrn = contributedSections.equals(savedContributedSections) && sectionComments.equals(savedSectionComments);
			if (savedStateCopied) {
				contributedSections.clear();
				sectionComments.clear();
				contributedSections.putAll(savedContributedSections);
				sectionComments.putAll(savedSectionComments);
			}
			savedContributedSections = null;
			savedSectionComments = null;
			return rtrn;
		}
	}

}
