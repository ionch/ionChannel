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

package social.ionch.api.social;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import social.ionch.api.text.RenderableText;

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
