package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
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

	/**
	 * Patterns to validate opening and closing tags
	 */
	private static final Pattern TAG_OPEN = Pattern.compile("\\[([^\\]/]+[^\\]]*)\\]"),
		TAG_CLOSE = Pattern.compile("\\[/([^\\]/]+)\\]");

	/**
	 * Tags for bulleted lists and a pattern to match numbered lists. The pattern is used so that
	 * we can figure out when to reset the counter. The site uses \r\n for line endings
	 */
	private static final String BULLET_LIST = "[*]";
	private static final String NUM_LIST = "[#]";

	private SpannableStringBuilder builder;

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
	 * Parse and apply styling to the bb text
	 *
	 * @return A Spannable containing the styled text
	 */
	public CharSequence parsebb(String bbText){
		builder = new SpannableStringBuilder(bbText);
		SmileyProcessor.bbSmileytoEmoji(builder);
		parseBulletLists();
		parseNumberedList();
		Stack<Tag> tags = new Stack<Tag>();
		//Run through the text and examine potential tags, parsing tags as we encounter them
		for (int start = indexOf(builder, "["), end = indexOf(builder, "]"); start != -1;
		     start = indexOf(builder, "[", start + 1), end = indexOf(builder, "]", start))
		{
			CharSequence block = builder.subSequence(start, end + 1);
			Matcher open = TAG_OPEN.matcher(block);
			//It's an opener
			if (open.find()){
				Tag t = new Tag(start, open.group(1));
				if (tagStyles.containsKey(t.tag)){
					start = openTag(tags, t);
				}
			}
			else {
				Matcher close = TAG_CLOSE.matcher(block);
				//If it's a closer
				if (close.find() && tagStyles.containsKey(close.group(1))){
					builder.delete(start, end + 1);
					//Pop-off and close all tags closed by this closer
					start = closeTags(tags, close.group(1), start);
				}
			}
		}
		//Clean up any tags that are being closed by the end of the text
		closeAllTags(tags);
		return builder;
	}

	/**
	 * Open a tag and handle any potential special cases
	 *
	 * @param tags tag stack to push the tag onto
	 * @param tag  the tag being opened
	 * @return the position to resume parsing from, required in the case of hidden tags
	 */
	private int openTag(Stack<Tag> tags, Tag tag){
		//Hidden tags have their content hidden so we extract it into the tag and don't waste time parsing
		//that content, since it'll only be shown if the hidden tag is clicked
		if (tag.tag.equalsIgnoreCase("hide")){
			return parseHiddenTag(tag);
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
	private int closeTags(Stack<Tag> tags, String tag, int start){
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
	private void closeAllTags(Stack<Tag> tags){
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
	private int parseHiddenTag(Tag t){
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
	public static int indexOf(SpannableStringBuilder ssb, String str, int start){
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

	public static int indexOf(SpannableStringBuilder ssb, String str){
		return indexOf(ssb, str, 0);
	}

	/**
	 * Parse any bulleted lists in the text and apply a bulleted list formatting to the list items
	 */
	private void parseBulletLists(){
		for (int start = indexOf(builder, BULLET_LIST), end = indexOf(builder, "\n", start); start != -1;
			 start = indexOf(builder, BULLET_LIST, end), end = indexOf(builder, "\n", start)){
			//If the last thing in the text is a list item then go to the end
			if (end == -1){
				end = builder.length() - 1;
			}
			builder.setSpan(new BulletSpan(BulletSpan.STANDARD_GAP_WIDTH), start + BULLET_LIST.length(), end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.delete(start, start + BULLET_LIST.length());
			//Account for the shorter length of the text with the tag removed
			end -= BULLET_LIST.length();
		}
	}

	/**
	 * Parse any numbered lists in the text and apply a numbered list formatting to the list items
	 */
	private void parseNumberedList(){
		int id = 1, prev = -1;
		for (int start = indexOf(builder, NUM_LIST), end = indexOf(builder, "\n", start); start != -1;
			 start = indexOf(builder, NUM_LIST, end), end = indexOf(builder, "\n", start)){
			//If the last thing in the text is a list item then go to the end
			if (end == -1){
				end = builder.length() - 1;
			}
			if (prev == start){
				++id;
			}
			else {
				id = 1;
			}
			String num = Integer.toString(id) + ". ";
			builder.replace(start, start + NUM_LIST.length(), num);
			//Account for the shorter length of the text with the tag removed
			prev = end - NUM_LIST.length() + num.length() + 1;
			end = prev;
		}
	}
}
