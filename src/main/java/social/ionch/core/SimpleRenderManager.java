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

package social.ionch.core;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.html.HtmlEscapers;

import social.ionch.api.text.RenderManager;
import social.ionch.api.text.RenderableText;
import social.ionch.api.text.TextRenderer;


public class SimpleRenderManager implements RenderManager {
	private HashMap<String, TextRenderer> renderers = new HashMap<>();
	
	
	protected SimpleRenderManager() {
		register("text/plain", (RenderableText r)->{
			String rendered = HtmlEscapers.htmlEscaper().escape(r.getSource());
			//TODO: Shortcodes
			r.setRendered(rendered);
		});
	}
	
	
	@Override
	public void register(String mimeType, TextRenderer renderer) {
		renderers.put(mimeType, renderer);
	}
	
	@Override
	@Nonnull
	public Set<String> getSupportedTypes() {
		return ImmutableSet.copyOf(renderers.keySet());
	}
	
	@Override
	public boolean render(RenderableText text) {
		TextRenderer r = renderers.get(text.getMimeType());
		if (r==null) return false;
		r.render(text);
		return true;
	}
	
	@Override
	@Nullable
	public TextRenderer getRenderer(String mimeType) {
		return renderers.get(mimeType);
	}
}
