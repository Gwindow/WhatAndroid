package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.*;
import what.whatandroid.imgloader.SmileyProcessor;

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
	private static final Pattern BOLD = Pattern.compile("\\[[bB]\\](.+?)(?:\\[\\/[bB]\\]|$)", Pattern.DOTALL),
		ITALIC = Pattern.compile("\\[[iI]\\](.+?)(?:\\[\\/[iI]\\]|$)", Pattern.DOTALL),
		UNDERLINE = Pattern.compile("\\[[uU]\\](.+?)(?:\\[\\/[uU]\\]|$)", Pattern.DOTALL),
		STRIKETHROUGH = Pattern.compile("\\[[sS]\\](.+?)(?:\\[\\/[sS]\\]|$)", Pattern.DOTALL),
		IMPORTANT = Pattern.compile("\\[(?:important|IMPORTANT)\\](.+?)(?:\\[\\/(?:important|IMPORTANT)\\]|$)", Pattern.DOTALL),
		CODE = Pattern.compile("\\[(?:code|CODE|pre|PRE)\\](.+?)(?:\\[\\/(?:code|CODE|pre|PRE)\\]|$)", Pattern.DOTALL),
		COLOR = Pattern.compile("\\[(?:color|COLOR)=([^\\]]+)\\](.+?)(?:\\[\\/(?:color|COLOR)\\]|$)"),
		ALIGN = Pattern.compile("\\[(?:align|ALIGN)=(\\w+)\\](.+?)(?:\\[\\/(?:align|ALIGN)\\]|$)", Pattern.DOTALL),
		SIZE = Pattern.compile("\\[(?:size|SIZE)=(\\d+)\\](.+?)(?:\\[\\/(?:size|SIZE)\\]|$)"),
		URL = Pattern.compile("\\[(?:url|URL)=?([^\\]]+)?\\](.+?)\\[\\/(?:url|URL)\\]"),
		IMG = Pattern.compile("\\[(?:img|IMG)=?([^\\]]+)?\\](?:(.+?)\\[\\/(?:img|IMG)\\])?"),
		QUOTE = Pattern.compile("\\[(?:quote|QUOTE)=?([^\\]]+)?\\](.+?)(?:\\[\\/(?:quote|QUOTE)\\]|$)", Pattern.DOTALL),
		HIDDEN = Pattern.compile("\\[(?:hide|HIDE|mature|MATURE)=?([^\\]]+)?\\](.+?)(?:\\[\\/(?:hide|HIDE|mature|MATURE)\\]|$)", Pattern.DOTALL),
		ARTIST = Pattern.compile("\\[(?:artist|ARTIST)\\](.+?)\\[\\/(?:artist|ARTIST)\\]", Pattern.DOTALL),
		USER = Pattern.compile("\\[(?:user|USER)\\](.+?)\\[\\/(?:user|USER)\\]", Pattern.DOTALL),
		TORRENT = Pattern.compile("\\[(?:torrent|TORRENT)\\](.+?)\\[\\/(?:torrent|TORRENT)\\]", Pattern.DOTALL);

	/**
	 * Tags for bulleted lists and a pattern to match numbered lists. The pattern is used so that
	 * we can figure out when to reset the counter. The site uses \r\n for line endings
	 */
	private static final String BULLET = "[*]";
	private static final Pattern NUM_LIST = Pattern.compile("\\[#\\][ ]*(.*)($|\r\n)");

	public static CharSequence parsebb(String bbText){
		SpannableStringBuilder ssb = new SpannableStringBuilder(bbText);
		StringBuilder text = new StringBuilder(bbText);
		SmileyProcessor.bbSmileytoEmoji(ssb, text);
		parseParameterizedTag(ssb, text, HIDDEN, new HiddenTag());
		parseSimpleTag(ssb, text, BOLD, new StyleSpan(Typeface.BOLD));
		parseSimpleTag(ssb, text, ITALIC, new StyleSpan(Typeface.ITALIC));
		parseSimpleTag(ssb, text, UNDERLINE, new UnderlineSpan());
		parseSimpleTag(ssb, text, STRIKETHROUGH, new StrikethroughSpan());
		parseSimpleTag(ssb, text, IMPORTANT, new ForegroundColorSpan(0xffff4444));
		parseSimpleTag(ssb, text, CODE, new TypefaceSpan("monospace"));
		parseParameterizedTag(ssb, text, ALIGN, new AlignTag());
		parseParameterizedTag(ssb, text, COLOR, new ColorTag());
		parseParameterizedTag(ssb, text, SIZE, new SizeTag());
		parseParameterizedTag(ssb, text, URL, new URLTag());
		parseParameterizedTag(ssb, text, ARTIST, new ArtistTag());
		parseParameterizedTag(ssb, text, TORRENT, new TorrentTag());
		parseParameterizedTag(ssb, text, QUOTE, new QuoteTag());
		parseParameterizedTag(ssb, text, IMG, new ImageTag());
		parseParameterizedTag(ssb, text, USER, new UserTag());
		parseBulletLists(ssb, text);
		parseNumberedList(ssb, text);
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
	private static void parseSimpleTag(SpannableStringBuilder ssb, StringBuilder text, Pattern tag, CharacterStyle cs){
		//Unfortunately because we're changing the text we need to reset each time. If this is too slow then maybe we
		//could just store the indices and do the replacement after finding all matches
		for (Matcher m = tag.matcher(text); m.find(); m.reset(text)){
			SpannableString styled = new SpannableString(m.group(1));
			styled.setSpan(CharacterStyle.wrap(cs), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//Get any existing spans in the range and apply them to the text so they'll be preserved if
			//we replace the entire section they span
			Object spans[] = ssb.getSpans(m.start(), m.end(), Object.class);
			if (spans != null){
				for (Object s : spans){
					//Figure out what range in the replacement to apply the span to
					int start = ssb.getSpanStart(s);
					int end = ssb.getSpanEnd(s);
					if (start <= m.start(1)){
						start = 0;
					}
					else {
						start -= m.start(1);
					}
					if (end >= m.end(1)){
						end = styled.length();
					}
					else {
						end = end - m.start(1);
					}
					styled.setSpan(s, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			ssb.replace(m.start(), m.end(), styled);
			text.replace(m.start(), m.end(), m.group(1));
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
	private static void parseParameterizedTag(SpannableStringBuilder ssb, StringBuilder text, Pattern tag, ParameterizedTag handler){
		//Unfortunately because we're changing the text we need to reset each time. If this is too slow then maybe we
		//could just store the indices and do the replacement after finding all matches
		for (Matcher m = tag.matcher(text); m.find(); m.reset(text)){
			Spannable styled;
			int groupStart, groupEnd;
			if (m.groupCount() == 2){
				styled = handler.getStyle(m.group(1), m.group(2));
				groupStart = m.start(2);
				groupEnd = m.end(2);
			}
			else {
				styled = handler.getStyle(m.group(1), null);
				groupStart = m.start(1);
				groupEnd = m.end(1);
			}
			//Get any existing spans in the range and apply them to the text so they'll be preserved if
			//we replace the entire section they span
			Object spans[] = ssb.getSpans(m.start(), m.end(), Object.class);
			if (spans != null){
				for (Object s : spans){
					//Figure out what range in the replacement to apply the span to
					int start = ssb.getSpanStart(s);
					int end = ssb.getSpanEnd(s);
					if (start <= groupStart){
						start = 0;
					}
					else {
						start -= groupStart;
					}
					if (end >= groupEnd){
						end = styled.length();
					}
					else {
						end = end - groupStart;
					}
					styled.setSpan(s, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			ssb.replace(m.start(), m.end(), styled);
			text.replace(m.start(), m.end(), styled.toString());
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
