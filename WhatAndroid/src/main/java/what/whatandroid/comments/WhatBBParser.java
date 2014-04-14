package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.*;

/**
 * Takes a bb formatted string and builds a formatted Spanned to display the text
 */
public class WhatBBParser {
	/**
	 * Tags for opening and closing a bold section of text
	 */
	private static final String[] BOLD = {"[b]", "[/b]"}, ITALIC = {"[i]", "[/i]"}, UNDERLINE = {"[u]", "[/u]"},
		STRIKETHROUGH = {"[s]", "[/s]"}, IMPORTANT = {"[important]", "[/important]"}, CODE = {"[code]", "[/code]"},
		PRE = {"[pre]", "[/pre]"};

	public static CharSequence parsebb(String bbText){
		SpannableStringBuilder ssb = new SpannableStringBuilder(bbText);
		StringBuilder text = new StringBuilder(bbText);
		parseSimpleTag(ssb, text, BOLD, new StyleSpan(Typeface.BOLD));
		parseSimpleTag(ssb, text, ITALIC, new StyleSpan(Typeface.ITALIC));
		parseSimpleTag(ssb, text, UNDERLINE, new UnderlineSpan());
		parseSimpleTag(ssb, text, STRIKETHROUGH, new StrikethroughSpan());
		parseSimpleTag(ssb, text, IMPORTANT, new ForegroundColorSpan(0xffff4444));
		parseSimpleTag(ssb, text, CODE, new TypefaceSpan("monospace"));
		parseSimpleTag(ssb, text, PRE, new TypefaceSpan("monospace"));
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
}
