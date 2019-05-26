/**
 * Text handling facilities.
 * 
 * <p>Any "content", "summary", or other "xsd:string | rdf:langString" field in ActivityPub can be a bit of a
 * headache to process: some fields allow markup, some do not, and all can optionally be a language-tagged map
 * of values. The classes in this package help deal with the combinatorial problem this presents, and help
 * steer serialized data towards predictable forms that Mastodon's narrow support will accept.
 * 
 * <p>In particular, Mastodon serializes some data that should be labeled as "text/markdown", as unlabeled/default
 * "text/html", and includes shortcode markup that they pretend isn't markup. So data coming in through mastodon
 * needs to be... massaged.
 * 
 * <p>Outgoing data can contain arbitrary markup, as long as the given ionch server has a TextRenderer for that markup.
 * The markup will be saved in a "source: { content: '', mediaType: '' }" envelope under its proper mime type, and a
 * rendered version will be saved in "content" and "mediaType" or "contentMap" and "mediaType" properties of the main object.
 */
package social.ionch.api.text;