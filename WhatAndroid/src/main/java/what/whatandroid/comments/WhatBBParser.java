package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import what.whatandroid.comments.tags.*;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes a bb formatted string and builds a formatted Spanned to display the text
 * Note: this doesn't properly handle nested tags of the same type, but I don't think
 * that's really used on the site so it's probably fine.
 */
public class WhatBBParser {
	private static final Map<String, TagStyle> tagStyles;
	private static final Pattern TAG_OPEN = Pattern.compile("\\[([^\\]/]+[^\\]]*)\\]"),
		TAG_CLOSE = Pattern.compile("\\[/([^\\]/]+)\\]");

	static{
		tagStyles = new TreeMap<String, TagStyle>(String.CASE_INSENSITIVE_ORDER);
		tagStyles.put("b", new BoldTagStyle());
		tagStyles.put("i", new ItalicTagStyle());
		tagStyles.put("u", new UnderlineTagStyle());
		tagStyles.put("s", new StrikethroughTagStyle());
		tagStyles.put("important", new ImportantTagStyle());
		TagStyle t = new CodeTagStyle();
		tagStyles.put("code", t);
		tagStyles.put("pre", t);
		tagStyles.put("align", new AlignTagStyle());
		tagStyles.put("color", new ColorTagStyle());
		tagStyles.put("size", new SizeTagStyle());
		tagStyles.put("url", new URLTagStyle());
		tagStyles.put("img", new ImageTagStyle());
		tagStyles.put("quote", new QuoteTagStyle());
		t = new HiddenTagStyle();
		tagStyles.put("hide", t);
		tagStyles.put("mature", t);
		tagStyles.put("artist", new ArtistTagStyle());
		tagStyles.put("user", new UserTagStyle());
		tagStyles.put("torrent", new TorrentTagStyle());
	}

	/**
	 * Tags for bulleted lists and a pattern to match numbered lists. The pattern is used so that
	 * we can figure out when to reset the counter. The site uses \r\n for line endings
	 */
	private static final String BULLET = "[*]";
	private static final Pattern NUM_LIST = Pattern.compile("\\[#\\][ ]*(.*)($|\r\n)");

	public static CharSequence parsebb(String bbText){
		SpannableStringBuilder builder = new SpannableStringBuilder(bbText);
		//SmileyProcessor.bbSmileytoEmoji(ssb, text);
		Stack<Tag> tags = new Stack<Tag>();

		for (int start = indexOf(builder, "["), end = indexOf(builder, "]"); start != -1;
		     start = indexOf(builder, "[", start + 1), end = indexOf(builder, "]", start))
		{
			CharSequence block = builder.subSequence(start, end + 1);
			Matcher open = TAG_OPEN.matcher(block);
			//It's an opener
			if (open.find()){
				Tag t = new Tag(start, open.group(1));
				if (tagStyles.containsKey(t.tag)){
					start = openTag(builder, tags, t);
				}
			}
			else {
				Matcher close = TAG_CLOSE.matcher(block);
				//If it's a closer
				if (close.find() && tagStyles.containsKey(close.group(1))){
					builder.delete(start, end + 1);
					//Pop-off and close all tags closed by this closer
					start = closeTags(builder, tags, close.group(1), start);
				}
			}
		}
		//Clean up any tags that are being closed by the end of the text
		closeAllTags(builder, tags);
		return builder;
	}

	/**
	 * Open a tag and handle any potential special cases
	 *
	 * @param tags tag stack to push the tag onto
	 * @param tag  the tag being opened
	 * @return the position to resume parsing from, required in the case of hidden tags
	 */
	private static int openTag(SpannableStringBuilder builder, Stack<Tag> tags, Tag tag){
		//Hidden tags have their content hidden so we extract it into the tag and don't waste time parsing
		//that content, since it'll only be shown if the hidden tag is clicked
		if (tag.tag.equalsIgnoreCase("hide")){
			return parseHiddenTag(builder, tag);
		}
		//If it's a self-closing image tag (the only type of tag that can self-close) handle the special case
		if (tag.tag.equalsIgnoreCase("img") && tag.param != null){
			tag.end = tag.start + tag.tagLength;
			builder.replace(tag.start, tag.end, tagStyles.get(tag.tag).getStyle(tag.param, null));
		}
		else {
			tags.push(tag);
		}
		return tag.start;
	}

	/**
	 * Close all tags up to and including tag in the stack of tags
	 *
	 * @param tag   tag name to close
	 * @param start the start of the closing tag
	 * @return location to resume parsing
	 */
	private static int closeTags(SpannableStringBuilder builder, Stack<Tag> tags, String tag, int start){
		if (tags.empty()){
			return start;
		}
		do {
			Tag t = tags.pop();
			t.end = start;
			Spannable styled = tagStyles.get(t.tag).getStyle(t.param, builder.subSequence(t.start + t.tagLength, t.end));
			builder.replace(t.start, t.end, styled);
			//Account for differences in the length of the previous text and its styled replacement
			start += getOffset(t, styled);
			//We seem to need this extra offset when closing multiple tags.
			//TODO find a proper fix for this instead of the patch below, unless that's the best we can do?
			if (!t.tag.equalsIgnoreCase(tag)){
				++start;
			}
			else {
				break;
			}
		}
		while (!tags.empty());
		return start;
	}

	/**
	 * Close all tags in the stack and end them at the end of the builder. Used to close any remaining open
	 * tags at the end of parsing, since these tags should run to the end of the text
	 */
	private static void closeAllTags(SpannableStringBuilder builder, Stack<Tag> tags){
		while (!tags.empty()){
			Tag t = tags.pop();
			t.end = builder.length();
			Spannable styled = tagStyles.get(t.tag).getStyle(t.param, builder.subSequence(t.start + t.tagLength, t.end));
			builder.replace(t.start, t.end, styled);
		}
	}

	/**
	 * Parse and conceal a hidden tag starting at some location
	 *
	 * @return the point after the end of the hidden text to resume parsing at
	 */
	private static int parseHiddenTag(SpannableStringBuilder builder, Tag t){
		String closer = "[/" + t.tag + "]";
		int s = indexOf(builder, closer, t.start);
		if (s == -1){
			s = builder.length();
		}
		else {
			builder.delete(s, s + closer.length());
		}
		t.end = s;
		Spannable styled = tagStyles.get(t.tag).getStyle(t.param, builder.subSequence(t.start + t.tagLength, t.end));
		builder.replace(t.start, t.end, styled);
		//Account for differences in the length of the previous text and its styled replacement
		s += getOffset(t, styled);
		return s;
	}

	/**
	 * Get the offset to add to the position after the tag when it's replaced by
	 * its styled text
	 *
	 * @param t      tag being replaced
	 * @param styled styled text replacing it
	 * @return offset to subtract from a position after the tag sequence replaced by the styled
	 * text to move it to the new ending position
	 */
	private static int getOffset(Tag t, CharSequence styled){
		return -(t.end - t.start - styled.length() + 1);
	}

	/**
	 * Find the index of the first occurrence of str after start in the spannable string builder
	 * -1 is returned if not found. SpannableStringBuilder lacks indexOf so we need to do it
	 * ourselves :(
	 */
	private static int indexOf(SpannableStringBuilder ssb, String str, int start){
		if (start > ssb.length()){
			return -1;
		}
		if (start < 0){
			start = 0;
		}
		for (int i = start; i < ssb.length(); ++i){
			if (ssb.charAt(i) == str.charAt(0)){
				int j = 1;
				for (int k = i + 1; j < str.length() && k < ssb.length(); ++j, ++k){
					if (ssb.charAt(k) != str.charAt(j)){
						i = k;
						break;
					}
				}
				if (j == str.length()){
					return i;
				}
			}
		}
		return -1;
	}

	private static int indexOf(SpannableStringBuilder ssb, String str){
		return indexOf(ssb, str, 0);
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
	private static void parseParameterizedTag(SpannableStringBuilder ssb, StringBuilder text, Pattern tag, TagStyle handler){
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
					//TODO: This is a temp fix until we put in a proper parser
					if (end > start && start >= 0){
						styled.setSpan(s, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
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
