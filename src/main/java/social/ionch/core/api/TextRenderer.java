package social.ionch.core.api;

import social.ionch.core.api.impl.RenderableText;

@FunctionalInterface
public interface TextRenderer {
	/**
	 * Takes the source text from this RenderableText and turns it into html markup, updating its
	 * rendered data with the new copy.
	 */
	public void render(RenderableText text);
}
