package social.ionch.api.text;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Renders markup from different scripts into full html
 */
public interface RenderManager {
	@Nonnull
	public Set<String> getSupportedTypes();
	public boolean render(RenderableText text);
	public void register(String mimeType, TextRenderer renderer);
	@Nullable
	public TextRenderer getRenderer(String mimeType);
}
