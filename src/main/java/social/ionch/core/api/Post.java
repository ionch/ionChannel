package social.ionch.core.api;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import social.ionch.core.api.impl.RenderableText;

public interface Post {
	
	/** Gets a permalink to this post. */
	public URI getURI();
	
	/** Gets the persistent id of the User that authored this post. */
	public int getAuthor();
	
	/** Gets the post's language, if specified, as a BCP47 identifier such as "tlh", "sjn", or the obscure "en-US". */
	@Nullable
	public String getLanguage();
	
	/** Gets the Content-Warning text for this Post. Empty text signifies that a CW is present but empty, but a null
	 * signifies that no CW exists for this post.
	 */
	@Nullable
	public RenderableText getCW();
	
	/** Gets the post body. */
	@Nonnull
	public RenderableText getPostBody();
}
