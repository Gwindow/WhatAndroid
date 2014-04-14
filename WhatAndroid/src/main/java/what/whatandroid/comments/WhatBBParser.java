package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

/**
 * Takes a bb formatted string and builds a formatted Spanned to display the text
 */
public class WhatBBParser {
	/**
	 * Tags for opening and closing a bold section of text
	 */
	private static final String[] BOLD = {"[b]", "[/b]"}, ITALIC = {"[i]", "[/i]"};

	public static CharSequence parsebb(String bbText){
		SpannableStringBuilder ssb = new SpannableStringBuilder(bbText);
		StringBuilder text = new StringBuilder(bbText);
		parseBold(ssb, text);
		parseItalic(ssb, text);
		return ssb;
	}

	/**
	 * Parse the bold tags in the string and apply formatting to them
	 */
	private static void parseBold(SpannableStringBuilder ssb, StringBuilder text){
		//Maybe pass this builder around everywhere as well? Less copying
		for (int s = text.indexOf(BOLD[0]) + BOLD[0].length(), e = text.indexOf(BOLD[1], s); s != -1 && e != -1;
			 s = text.indexOf(BOLD[0], s) + BOLD[0].length(), e = text.indexOf(BOLD[1], s)){
			ssb.setSpan(new StyleSpan(Typeface.BOLD), s, e, 0);
			//Remove the open and close tags
			ssb.delete(s - BOLD[0].length(), s);
			text.delete(s - BOLD[0].length(), s);
			e -= BOLD[0].length();
			ssb.delete(e, e + BOLD[1].length());
			text.delete(e, e + BOLD[1].length());
		}
	}

	/**
	 * Parse the italic tags in the string and apply formatting to them
	 */
	private static void parseItalic(SpannableStringBuilder ssb, StringBuilder text){
		//Maybe pass this builder around everywhere as well? Less copying
		for (int s = text.indexOf(ITALIC[0]) + ITALIC[0].length(), e = text.indexOf(ITALIC[1], s); s != -1 && e != -1;
			 s = text.indexOf(ITALIC[0], s) + ITALIC[0].length(), e = text.indexOf(ITALIC[1], s)){
			ssb.setSpan(new StyleSpan(Typeface.ITALIC), s, e, 0);
			//Remove the open and close tokens
			ssb.delete(s - ITALIC[0].length(), s);
			text.delete(s - ITALIC[0].length(), s);
			e -= ITALIC[0].length();
			ssb.delete(e, e + ITALIC[1].length());
			text.delete(e, e + ITALIC[1].length());
		}
	}
}
