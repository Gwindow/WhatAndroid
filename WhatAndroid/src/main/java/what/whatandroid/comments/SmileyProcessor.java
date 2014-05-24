package what.whatandroid.comments;

import android.text.SpannableStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run through some user html comment text and substitute the smiley images
 * with corresponding emoji so Android will show its build in smileys
 */
public class SmileyProcessor {
	/**
	 * A map of all supported site emoticons to the equivalent (or sort of equivalent) emojis
	 * Note: The HTML side of this parsing should be removed once we can get the BB formatted
	 * text for all site content
	 */
	private static final Map<String, Pattern> emojisHtml;

	/**
	 * Emoji bb codes used by the site, not that all characters are lowercase since
	 * the text is converted to lower case to simplify parsing
	 */
	private static final Map<String, String> emojisBB;


	/**
	 * Scan through the passed what html user post and replace references to the site
	 * smilies with corresponding emoticons for emoji to pick up
	 */
	public static String smileyToEmoji(String s){
		for (Map.Entry<String, Pattern> emoji : emojisHtml.entrySet()){
			Matcher m = emoji.getValue().matcher(s);
			s = m.replaceAll(emoji.getKey());
		}
		return s;
	}

	/**
	 * Find site bb formatted smilies in the text and convert them to emoji characters in the text
	 * and the spannable string
	 *
	 * @param ssb  spannable string to convert emojis in
	 */
	public static void bbSmileytoEmoji(SpannableStringBuilder ssb){
		//Take care of emoticons that have others as their prefix, ie. :ohnoes:, :ohno:, :omg:
		//:ohno: is a prefix of :ohnoes: and :o is a prefix of all 3
		String[] conflicts = {":ohnoes:", ":ohno:", ":omg:"};
		for (String emoticon : conflicts){
			String emoji = emojisBB.get(emoticon);
			for (int i = WhatBBParser.indexOf(ssb, emoticon); i != -1; i = WhatBBParser.indexOf(ssb, emoticon, i)){
				ssb.replace(i, i + emoticon.length(), emoji);
			}
		}
		for (Map.Entry<String, String> e : emojisBB.entrySet()){
			for (int i = WhatBBParser.indexOf(ssb, e.getKey()); i != -1; i = WhatBBParser.indexOf(ssb, e.getKey(), i)){
				ssb.replace(i, i + e.getKey().length(), e.getValue());
			}
		}
	}

	static{
		emojisHtml = new HashMap<String, Pattern>();
		emojisHtml.put("&#x1f621;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(angry|paddle).gif\"[^>]*>)"));
		emojisHtml.put("&#x1f604;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/biggrin.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f610;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/blank.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f633;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/blush.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f60e;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/cool.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f622;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(crying|sad|sorry).gif\"[^>]*>)"));
		emojisHtml.put("&#x1f619;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/eyesright.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f60f;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/creepy.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f61e;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(frown|no).gif\"[^>]*>)"));
		emojisHtml.put("&#x2764;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/heart.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f614;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/hmm.gif\"[^>]*>)"));
		emojisHtml.put("&#x2764; What.CD", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ilu.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f606;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/laughing.gif\"[^>]*>)"));
		emojisHtml.put("&#x2764; FLAC", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/loveflac.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f637;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ninja.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f601;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/nod.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f628;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ohnoes.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f632;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(omg|ohshit|wtf).gif\"[^>]*>)"));
		emojisHtml.put("&#x1f612;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/shifty.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f623;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/sick.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f60a;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/smile.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f604; Thanks!", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/thanks.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f60b;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/tongue.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f44b;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wave.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f609;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wink.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f613;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/worried.gif\"[^>]*>)"));
		emojisHtml.put("&#x1f60d;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wub.gif\"[^>]*>)"));

		emojisBB = new HashMap<String, String>();
		emojisBB.put(":angry:", "\uD83D\uDE21");
		emojisBB.put(":paddle:", "\uD83D\uDE21");
		emojisBB.put(":D", "\uD83D\uDE04");
		emojisBB.put(":-D", "\uD83D\uDE04");
		emojisBB.put(":|", "\uD83D\uDE10");
		emojisBB.put(":-|", "\uD83D\uDE10");
		emojisBB.put(":blush:", "\uD83D\uDE33");
		emojisBB.put(":cool:", "\uD83D\uDE0E");
		emojisBB.put(":'(", "\uD83D\uDE22");
		emojisBB.put(":sorry:", "\uD83D\uDE22");
		emojisBB.put(">.>", "\uD83D\uDE19");
		emojisBB.put(":creepy:", "\uD83D\uDE0F");
		emojisBB.put(":frown:", "\uD83D\uDE20");
		emojisBB.put("<3", "\u2764\uFE0F");
		emojisBB.put(":unsure:", "\uD83D\uDE1F");
		emojisBB.put(":no:", "\uD83D\uDE1F");
		emojisBB.put(":whatlove:", "\u2764\uFE0F What.CD");
		emojisBB.put(":lol:", "\uD83D\uDE06");
		emojisBB.put(":loveflac:", "\u2764\uFE0F FLAC");
		emojisBB.put(":flaclove:", "\u2764\uFE0F FLAC");
		emojisBB.put(":ninja:", "\uD83D\uDE37");
		emojisBB.put(":nod:", "\uD83D\uDE0A");
		emojisBB.put(":ohno:", "\uD83D\uDE27");
		emojisBB.put(":ohnoes:", "\uD83D\uDE27");
		emojisBB.put(":omg:", "\uD83D\uDE32");
		emojisBB.put(":o", "\uD83D\uDE32");
		emojisBB.put(":O", "\uD83D\uDE32");
		emojisBB.put(":wtf:", "\uD83D\uDE32");
		emojisBB.put(":(", "\uD83D\uDE1E");
		emojisBB.put(":-(", "\uD83D\uDE1E");
		emojisBB.put(":shifty:", "\uD83D\uDE12");
		emojisBB.put(":sick:", "\uD83D\uDE30");
		emojisBB.put(":)", "\uD83D\uDE00");
		emojisBB.put(":-)", "\uD83D\uDE00");
		emojisBB.put(":thanks:", "\uD83D\uDE04 Thanks!");
		emojisBB.put(":P", "\uD83D\uDE1B");
		emojisBB.put(":-P", "\uD83D\uDE1B");
		emojisBB.put(":-p", "\uD83D\uDE1B");
		emojisBB.put(":wave:", "\uD83D\uDC4B");
		emojisBB.put(":wink:", "\uD83D\uDE09");
		emojisBB.put(":worried:", "\uD83D\uDE2F");
		emojisBB.put(":wub:", "\uD83D\uDE0D");
	}
}
