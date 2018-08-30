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

package social.ionch.api.text;

import com.google.common.html.HtmlEscapers;

/**
 * Represents some text which a user entered, which is then rendered as HTML for viewing.
 */
public class RenderableText {
	private String mimeType = "text/plain";
	private String source = "";
	private String derived = "";
	
	public RenderableText() {}
	public RenderableText(String s) {
		mimeType = "text/plain";
		source = s;
		derived = HtmlEscapers.htmlEscaper().escape(s);
	}
	
	/**
	 * Gets the MIME type of the source text. Usually this is "text/plain", "text/markdown", or "text/html".
	 * This method MUST NOT include Charset information in its return value. This is because the data is already
	 * represented in Java's UTF16BE variant, and extra information complicates String matching against known
	 * MIME types.
	 */
	public String getMimeType() { return mimeType; }
	
	/**
	 * Gets the source text, whose mime type is specified by {@link #getMimeType()}. This helps support
	 * "delete-and-redraft" functionality while respecting the author's original editing format where possible.
	 */
	public String getSource() { return source; }
	
	/**
	 * Gets the result of converting the source text into HTML. This will almost certainly contain HTML markup.
	 */
	public String getRendered() { return derived; }
	
	
	public void setMimeType(String mimeType) { this.mimeType = mimeType; }
	public void setSource(String source) { this.source = source; }
	public void setRendered(String rendered) { this.derived = rendered; }
}
