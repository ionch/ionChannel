package social.ionch.api;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import social.ionch.api.db.Database;
import social.ionch.api.text.RenderableText;
import social.ionch.api.text.TextRenderer;

public class Ionch {
	private ArrayList<Database> databases = new ArrayList<>();
	private Database defaultDatabase;
	private HashMap<String, TextRenderer> renderers = new HashMap<>();
	
	@Nonnull
	public Database getDatabase() {
		return defaultDatabase;
	}
	
	@Nullable
	public TextRenderer getTextRenderer(String mimeType) {
		return renderers.get(mimeType);
	}
	
	public boolean render(RenderableText text) {
		TextRenderer renderer = renderers.get(text.getMimeType());
		if (renderer==null) return false;
		renderer.render(text);
		return true;
	}
	
	public void registerDatabase(Database database) {
		if (databases.contains(database)) return;
		databases.add(database);
		if (defaultDatabase==null) defaultDatabase = database;
	}
}
