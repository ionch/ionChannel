package social.ionch.core.api.impl;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.html.HtmlEscapers;

import social.ionch.core.api.TextRenderer;

/**
 * Renders markup from different scripts into full html
 */
public class Render {
	private static HashMap<String, TextRenderer> renderers = new HashMap<>();
	
	
	static {
		register("text/plain", (RenderableText r)->{
			String rendered = HtmlEscapers.htmlEscaper().escape(r.getSource());
			//TODO: Shortcodes
			r.setRendered(rendered);
		});
	}
	
	
	
	
	public static void register(String mimeType, TextRenderer renderer) {
		renderers.put(mimeType, renderer);
	}
	
	public static Set<String> getSupportedTypes() {
		return ImmutableSet.copyOf(renderers.keySet());
	}
	
	public static boolean render(RenderableText text) {
		TextRenderer r = renderers.get(text.getMimeType());
		if (r==null) return false;
		r.render(text);
		return true;
	}
}
