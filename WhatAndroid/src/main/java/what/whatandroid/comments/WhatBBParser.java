package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes a bb formatted string and builds a formatted Spanned to display the text
 * Note: this doesn't properly handle nested tags of the same type, but I don't think
 * that's really used on the site so it's probably fine.
 */
public class WhatBBParser {
	/**
	 * Tags for opening and closing a bold section of text
	 */
	private static final String[] BOLD = {"[b]", "[/b]"}, ITALIC = {"[i]", "[/i]"}, UNDERLINE = {"[u]", "[/u]"},
		STRIKETHROUGH = {"[s]", "[/s]"}, IMPORTANT = {"[important]", "[/important]"}, CODE = {"[code]", "[/code]"},
		PRE = {"[pre]", "[/pre]"}, ALIGN = {"[align=", "[/align]"}, COLOR = {"[color=", "[/color]"},
		SIZE = {"[size=", "[/size]"}, URL = {"[url", "[/url]"}, ARTIST = {"[artist", "[/artist]"},
		TORRENT = {"[torrent", "[/torrent]"}, USER = {"[user", "[/user]"}, QUOTE = {"[quote", "[/quote]"},
		HIDDEN = {"[hide", "[/hide]"}, MATURE = {"[mature", "[/mature]"};
	/**
	 * Tags for bulleted lists and a pattern to match numbered lists. The pattern is used so that
	 * we can figure out when to reset the counter. The site uses \r\n for line endings
	 */
	private static final String BULLET = "[*]";
	private static final Pattern NUM_LIST = Pattern.compile("\\[#\\][ ]*(.*)($|\r\n)");

	public static CharSequence parsebb(String bbText){
		SpannableStringBuilder ssb = new SpannableStringBuilder(bbText);
		StringBuilder text = new StringBuilder(bbText.toLowerCase());
		parseSimpleTag(ssb, text, BOLD, new StyleSpan(Typeface.BOLD));
		parseSimpleTag(ssb, text, ITALIC, new StyleSpan(Typeface.ITALIC));
		parseSimpleTag(ssb, text, UNDERLINE, new UnderlineSpan());
		parseSimpleTag(ssb, text, STRIKETHROUGH, new StrikethroughSpan());
		parseSimpleTag(ssb, text, IMPORTANT, new ForegroundColorSpan(0xffff4444));
		parseSimpleTag(ssb, text, CODE, new TypefaceSpan("monospace"));
		parseSimpleTag(ssb, text, PRE, new TypefaceSpan("monospace"));
		parseParameterizedTag(ssb, text, ALIGN, new AlignTag());
		parseParameterizedTag(ssb, text, COLOR, new ColorTag());
		parseParameterizedTag(ssb, text, SIZE, new SizeTag());
		parseParameterizedTag(ssb, text, URL, new URLTag());
		parseParameterizedTag(ssb, text, ARTIST, new ArtistTag());
		parseParameterizedTag(ssb, text, TORRENT, new TorrentTag());
		parseQuoteTag(ssb, text);
		parseBulletLists(ssb, text);
		parseNumberedList(ssb, text);
		parseHiddenTags(ssb, text, HIDDEN);
		parseHiddenTags(ssb, text, MATURE);
		return ssb;
	}

	/**
	 * Parse a simple tag passed and apply the styling desired. A simple tag is bold, italic, underline or similar
	 * where no extra information from the tag is required to decide the formatting.
	 *
	 * @param ssb  spannable string builder to apply the styling in
	 * @param text a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *             removed in here and in the ssb to keep them matching
	 * @param tag  tag[0] is the opening tag, tag[1] is the closing tag
	 * @param cs   character style to apply to the text in the tag
	 */
	private static void parseSimpleTag(SpannableStringBuilder ssb, StringBuilder text, String tag[], CharacterStyle cs){
		for (int s = text.indexOf(tag[0]) + tag[0].length(), e = text.indexOf(tag[1], s); s != -1 && e != -1;
			 s = text.indexOf(tag[0], s) + tag[0].length(), e = text.indexOf(tag[1], s)){
			ssb.setSpan(CharacterStyle.wrap(cs), s, e, 0);
			//Remove the open and close tokens
			ssb.delete(s - tag[0].length(), s);
			text.delete(s - tag[0].length(), s);
			e -= tag[0].length();
			ssb.delete(e, e + tag[1].length());
			text.delete(e, e + tag[1].length());
		}
	}

	/**
	 * Parse parameterized tags in the text and apply the style returned by the ParameterizedTag handler
	 * Tag openers should be of the form '[tag=' to make finding the parameter simple, or if the parameter is
	 * optional the tag should be '[tag' so that the case of no parameter can be found as well. Even if the
	 * parameter is only passed through the text the tag should still be '[tag' for it to be found correctly
	 *
	 * @param ssb     spannable string builder to apply the styling in
	 * @param text    a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *                removed in here and in the ssb to keep them matching
	 * @param tag     tag[0] is the opening tag, tag[1] is the closing tag
	 * @param handler handler to return the appropriate tag for the parameters
	 */
	private static void parseParameterizedTag(SpannableStringBuilder ssb, StringBuilder text, String tag[], ParameterizedTag handler){
		for (int s = text.indexOf(tag[0]) + tag[0].length(), e = text.indexOf(tag[1], s); s != -1 && e != -1;
			 s = text.indexOf(tag[0], s) + tag[0].length(), e = text.indexOf(tag[1], s)){
			//Find the tag parameter and handle optional parameters
			int openerClose = text.indexOf("]", s);
			String param;
			if (text.charAt(s) == '='){
				param = ssb.subSequence(s + 1, openerClose).toString();
			}
			else {
				param = ssb.subSequence(s, openerClose).toString();
			}
			ssb.setSpan(handler.getStyle(param, ssb.subSequence(openerClose + 1, e).toString()), openerClose + 1, e, 0);
			//Remove the open and close tokens
			ssb.delete(s - tag[0].length(), openerClose + 1);
			text.delete(s - tag[0].length(), openerClose + 1);
			e -= openerClose + 1 - s + tag[0].length();
			ssb.delete(e, e + tag[1].length());
			text.delete(e, e + tag[1].length());
		}
	}

	/**
	 * Parse quote tags in the text and apply the quote style and if a user was specified in the quote put
	 * their name above it, similar to the site
	 *
	 * @param ssb  spannable string builder to apply the styling in
	 * @param text a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *             removed in here and in the ssb to keep them matching
	 */
	private static void parseQuoteTag(SpannableStringBuilder ssb, StringBuilder text){
		for (int s = text.indexOf(QUOTE[0]) + QUOTE[0].length(), e = text.indexOf(QUOTE[1], s); s != -1 && e != -1;
			 s = text.indexOf(QUOTE[0], s) + QUOTE[0].length(), e = text.indexOf(QUOTE[1], s)){
			//Find the quote parameter if there is one
			int openerClose = text.indexOf("]", s);
			String user = "";
			if (text.charAt(s) == '='){
				user = ssb.subSequence(s + 1, openerClose).toString();
				//We ignore the post id links in quotes
				int nameEnd = user.indexOf('|');
				if (nameEnd != -1){
					user = user.substring(0, nameEnd);
				}
				user += " wrote:\n";
			}
			if (!user.isEmpty()){
				ssb.insert(openerClose + 1, user);
				text.insert(openerClose + 1, user);
				ssb.setSpan(new StyleSpan(Typeface.BOLD), openerClose + 1, openerClose + 1 + user.length(), 0);
				e += user.length();
			}
			ssb.setSpan(new QuoteSpan(0xff33b5e5), openerClose + 1 + user.length(), e, 0);
			//Remove the open and close tokens
			ssb.delete(s - QUOTE[0].length(), openerClose + 1);
			text.delete(s - QUOTE[0].length(), openerClose + 1);
			e -= openerClose + 1 - s + QUOTE[0].length();
			ssb.delete(e, e + QUOTE[1].length());
			text.delete(e, e + QUOTE[1].length());
		}
	}

	/**
	 * Parse hidden and mature tags and sets them to be clickable spans that will show the hidden text when clicked
	 * via a callback to the context they're attached in. If the callback isn't implemented nothing will be shown
	 * The hidden tags should be parsed after all other styes have been applied, otherwise the hidden text may be
	 * missing some of its markup when shown in the pop-up since it's removed here and passed to the hidden text span
	 *
	 * @param ssb  spannable string builder to apply the styling in
	 * @param text a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *             removed in here and in the ssb to keep them matching
	 * @param tag  the tags to look for, either hidden or mature, both are formatted the same way
	 */
	private static void parseHiddenTags(SpannableStringBuilder ssb, StringBuilder text, String tag[]){
		for (int s = text.indexOf(tag[0]) + tag[0].length(), e = text.indexOf(tag[1], s); s != -1 && e != -1;
			 s = text.indexOf(tag[0], s) + tag[0].length(), e = text.indexOf(tag[1], s)){
			int openerClose = text.indexOf("]", s);
			String description;
			if (text.charAt(s) == '='){
				description = ssb.subSequence(s + 1, openerClose).toString();
				if (tag[0].contains("mature")){
					description = "Mature content: " + description;
				}
				else {
					description = "Show hidden text: " + description;
				}
			}
			//Only hidden text can be without a description so pick that title
			else {
				description = "Show hidden text";
			}
			HiddenTextSpan hiddenSpan = new HiddenTextSpan(description, ssb.subSequence(openerClose + 1, e));
			//Remove the tags and hidden text and replace with the description
			s -= tag[0].length();
			ssb.delete(s, e + tag[1].length());
			text.delete(s, e + tag[1].length());
			ssb.insert(s, description);
			text.insert(s, description);
			ssb.setSpan(new StyleSpan(Typeface.BOLD), s, s + description.length(), 0);
			ssb.setSpan(hiddenSpan, s, s + description.length(), 0);
		}
	}

	/**
	 * Parse any bulleted lists in the text and apply a bulleted list formatting to the list items
	 *
	 * @param ssb  spannable string builder to apply the styling in
	 * @param text a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *             removed in here and in the ssb to keep them matching
	 */
	private static void parseBulletLists(SpannableStringBuilder ssb, StringBuilder text){
		for (int s = text.indexOf(BULLET), e = text.indexOf("\n", s); s != -1; s = text.indexOf(BULLET, e), e = text.indexOf("\n", s)){
			//If the last thing in the text is a list item then go to the end
			if (e == -1){
				e = text.length() - 1;
			}
			ssb.setSpan(new BulletSpan(BulletSpan.STANDARD_GAP_WIDTH), s + BULLET.length(), e, 0);
			ssb.delete(s, s + BULLET.length());
			text.delete(s, s + BULLET.length());
			e -= BULLET.length();
		}
	}

	/**
	 * Parse any numbered lists in the text and apply a numbered list formatting to the list items
	 *
	 * @param ssb  spannable string builder to apply the styling in
	 * @param text a mirror of the text in the spannable string builder, used to look up tag positions and tags will be
	 *             removed in here and in the ssb to keep them matching
	 */
	private static void parseNumberedList(SpannableStringBuilder ssb, StringBuilder text){
		int id = 1, prev = -1;
		//Because we're changing the text as we match over it we need to reset the matcher each iteration
		for (Matcher m = NUM_LIST.matcher(text); m.find(); m.reset(text)){
			if (prev == m.start()){
				++id;
			}
			else {
				id = 1;
			}
			ssb.delete(m.start(), m.start(1));
			text.delete(m.start(), m.start(1));
			//Insert the number for the list item and update the previous index to reflect the changes to the string
			String num = Integer.toString(id) + ". ";
			ssb.insert(m.start(), num);
			text.insert(m.start(), num);
			prev = m.end() - m.start(1) + m.start() + num.length();
		}
	}
}
