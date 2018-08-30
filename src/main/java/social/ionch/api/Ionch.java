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

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import social.ionch.api.db.Database;
import social.ionch.api.module.ModuleLoader;
import social.ionch.api.text.RenderableText;
import social.ionch.api.text.TextRenderer;

public class Ionch {
	private static ArrayList<Database> databases = new ArrayList<>();
	private static Database defaultDatabase;
	private static HashMap<String, TextRenderer> renderers = new HashMap<>();
	private static ModuleLoader moduleLoader;
	
	@Nonnull
	public static Database getDatabase() {
		return defaultDatabase;
	}
	
	@Nullable
	public static TextRenderer getTextRenderer(String mimeType) {
		return renderers.get(mimeType);
	}
	
	public static boolean render(RenderableText text) {
		TextRenderer renderer = renderers.get(text.getMimeType());
		if (renderer==null) return false;
		renderer.render(text);
		return true;
	}
	
	public static void registerDatabase(Database database) {
		if (databases.contains(database)) return;
		databases.add(database);
		if (defaultDatabase==null) defaultDatabase = database;
	}
	
	public static ModuleLoader getModuleLoader() {
		return moduleLoader;
	}
	
	/** WILL OVERWRITE THE DEFAULT MODULE LOADER! For internal use only. */
	public static void registerModuleLoader(ModuleLoader loader) {
		moduleLoader = loader;
	}
}
