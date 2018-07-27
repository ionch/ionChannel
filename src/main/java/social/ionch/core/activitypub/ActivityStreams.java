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

package social.ionch.core.activitypub;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

/*
 * Striking up interfaces for the basic Activity Vocabulary ( https://www.w3.org/TR/2017/REC-activitystreams-vocabulary-20170523/#introduction )
 * 
 * Mostly just to get organized. This is *a* way to represent AV, and we'll see if it's a good way soon as details and edge cases start to pile up.
 * 
 * 
 * 
 * At first blush, what I'm noticing is that Object is hopelessly unwieldy. We're going to have to decide on a blessed subset of these values which
 * are permitted to occur in specific activitypub implementation objects. Unblessed keys (or, in fact, list items) will have to be ignored.
 */

public interface ActivityStreams {
	
	public interface Element {
		String getType();
		String toJson();
		void loadJson(String json);
	}
	
	public interface Object extends Element, Attachment {
		@Override
		default String getType() { return "Object"; }
		
		String getId();
		String getName();
		@Nullable List<Attachment> getAttachments();
		@Nullable List<Attachment> getTags();
		@Nullable List<Attribution> getAttributedTo();
		@Nullable List<Element> getAudience(); //Object | Link
		@Nullable List<Attribution> getTo();
		@Nullable List<Attribution> getBTo();
		@Nullable List<Attribution> getCC();
		@Nullable List<Attribution> getBCC();
		
		/** This is dumb. Do not use. */
		@Nullable List<Element> getReplies();
		/** This is a sensible list. Do use. */
		@Nullable List<Element> getInReplyTo();
		
		String getContentType();
		
		/** By default, the values in this map are HTML. See {@link #getContentType()} */
		@Nullable Map<Locale, String> getContentMap();
		/** Semantics unspecified except that objects which share a context should have a common origin or purpose. We are welcome to use for whatever kinds of grouping we need. */
		Element getContext();
		
		/** An xsd:DateTime object representing the beginning of an activity */
		String getStartTime();
		/** An xsd:DateTime object representing the end of an activity */
		String getEndTime();
		/** An xsd:DateTime object representing the time of publication, not the time the activity went into effect. */
		String getPublished();
		/** An xsd:DateTime the object was last edited */
		String getUpdated();
		
		Attribution getGenerator();
		Element getIcon();
		Element getImage();
		Element getLocation();
		Element getPreview();
		Element getSummary();
		
		Link getUrl();
	}
	
	/** What it says on the tin. */
	public interface Link extends Element, Attachment, Attribution {
		@Override
		default String getType() { return "Link"; }
		
		/** A link relation conforming to both HTML5 and RFC5988 */
		String getRel();
		/** xsd:anyURI - this field is somewhat flexible. */
		String getHref();
		/** A BCP47 language tag, such as "en" */
		String getHrefLang();
		/** A MIME type, such as "text/html" */
		String getMediaType();
		/** Map of locales to names for this resource. If no language is specified, it will be presented here as "en". Note: Markup is <em>forbidden</em> in values.*/
		Map<Locale, String> getNameMap();
	}
	
	public interface Activity extends Element {
		@Override
		default String getType() { return "Activity"; }
		
		String getSummary();
		Actor getActor();
		Actor getObject();
		@Nullable Instrument getInstrument();
		
	}
	
	/**
	 * Describes a service, device, or protocol used to accomplish an Actor's action.
	 */
	public interface Instrument extends Object {
		String getType();
		String getName();
	}
	
	public interface Actor extends Object, Attribution {
		/** Application, Group, Organization, Person, or Service */
		String getType();
		String getName();
	}
	
	
	
	/** Tagging interface for elements which can be Attachments */
	public interface Attachment {}
	
	/** Tagging interface for elements which can appear as "attributedTo" entries */
	public interface Attribution {}
}
