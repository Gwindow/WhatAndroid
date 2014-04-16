package what.whatandroid.imgloader;

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
	 */
	private static final Map<String, Pattern> emojis = new HashMap<String, Pattern>() {{
		put("&#x1f621;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(angry|paddle).gif\"[^>]*>)"));
		put("&#x1f604;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/biggrin.gif\"[^>]*>)"));
		put("&#x1f610;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/blank.gif\"[^>]*>)"));
		put("&#x1f633;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/blush.gif\"[^>]*>)"));
		put("&#x1f60e;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/cool.gif\"[^>]*>)"));
		put("&#x1f622;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(crying|sad|sorry).gif\"[^>]*>)"));
		put("&#x1f619;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/eyesright.gif\"[^>]*>)"));
		put("&#x1f60f;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/creepy.gif\"[^>]*>)"));
		put("&#x1f61e;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(frown|no).gif\"[^>]*>)"));
		put("&#x2764;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/heart.gif\"[^>]*>)"));
		put("&#x1f614;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/hmm.gif\"[^>]*>)"));
		put("&#x2764; What.CD", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ilu.gif\"[^>]*>)"));
		put("&#x1f606;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/laughing.gif\"[^>]*>)"));
		put("&#x2764; FLAC", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/loveflac.gif\"[^>]*>)"));
		put("&#x1f637;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ninja.gif\"[^>]*>)"));
		put("&#x1f601;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/nod.gif\"[^>]*>)"));
		put("&#x1f628;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/ohnoes.gif\"[^>]*>)"));
		put("&#x1f632;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/(omg|ohshit|wtf).gif\"[^>]*>)"));
		put("&#x1f612;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/shifty.gif\"[^>]*>)"));
		put("&#x1f623;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/sick.gif\"[^>]*>)"));
		put("&#x1f60a;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/smile.gif\"[^>]*>)"));
		put("&#x1f604; Thanks!", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/thanks.gif\"[^>]*>)"));
		put("&#x1f60b;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/tongue.gif\"[^>]*>)"));
		put("&#x1f44b;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wave.gif\"[^>]*>)"));
		put("&#x1f609;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wink.gif\"[^>]*>)"));
		put("&#x1f613;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/worried.gif\"[^>]*>)"));
		put("&#x1f60d;", Pattern.compile("(<img [^>]*src=\"[^\"]+smileys/wub.gif\"[^>]*>)"));
	}};

	/**
	 * Emoji bb codes used by the site, not that all characters are lowercase since
	 * the text is converted to lower case to simplify parsing
	 */
	private static final Map<String, Pattern> emojisBB = new HashMap<String, Pattern>() {{
		put("\uD83D\uDE21", Pattern.compile("(:angry:|:paddle:)"));
		put("\uD83D\uDE04", Pattern.compile("(:D|:-D)"));
		put("\uD83D\uDE10", Pattern.compile("(:\\||:-\\|)"));
		put("\uD83D\uDE33", Pattern.compile("(:blush:)"));
		put("\uD83D\uDE0E", Pattern.compile("(:cool:)"));
		put("\uD83D\uDE22", Pattern.compile("(:'\\(|:sorry:)"));
		put("\uD83D\uDE19", Pattern.compile("(>\\.>)"));
		put("\uD83D\uDE0F", Pattern.compile("(:creepy:)"));
		put("\uD83D\uDE20", Pattern.compile("(:frown:)"));
		put("\u2764\uFE0F", Pattern.compile("(<3)"));
		put("\uD83D\uDE1F", Pattern.compile("(:unsure:|:no:)"));
		put("\u2764\uFE0F What.CD", Pattern.compile("(:whatlove:)"));
		put("\uD83D\uDE06", Pattern.compile("(:lol:)"));
		put("\u2764\uFE0F FLAC", Pattern.compile("(:loveflac:|:flaclove:)"));
		put("\uD83D\uDE37", Pattern.compile("(:ninja:)"));
		put("\uD83D\uDE0A", Pattern.compile("(:nod:)"));
		put("\uD83D\uDE27", Pattern.compile("(:ohno:|:ohnoes:)"));
		put("\uD83D\uDE32", Pattern.compile("(:omg:|:o|:O|:wtf:)"));
		put("\uD83D\uDE1E", Pattern.compile("(:\\(|:-\\()"));
		put("\uD83D\uDE12", Pattern.compile("(:shifty:)"));
		put("\uD83D\uDE30", Pattern.compile("(:sick:)"));
		put("\uD83D\uDE00", Pattern.compile("(:\\)|:-\\))"));
		put("\uD83D\uDE04 Thanks!", Pattern.compile("(:thanks:)"));
		put("\uD83D\uDE1B", Pattern.compile("(:P|:-P|:-p)"));
		put("\uD83D\uDC4B", Pattern.compile("(:wave:)"));
		put("\uD83D\uDE09", Pattern.compile("(:wink:)"));
		put("\uD83D\uDE2F", Pattern.compile("(:worried:)"));
		put("\uD83D\uDE0D", Pattern.compile("(:wub:)"));
	}};

	/**
	 * Scan through the passed what html user post and replace references to the site
	 * smilies with corresponding emoticons for emoji to pick up
	 * I'm not sure about the speed of this, but it's neat to try out
	 */
	public static String smileyToEmoji(String s){
		for (Map.Entry<String, Pattern> emoji : emojis.entrySet()){
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
	 * @param text text mirroring the spannable string to use for lookups
	 */
	public static void bbSmileytoEmoji(SpannableStringBuilder ssb, StringBuilder text){
		for (Map.Entry<String, Pattern> e : emojisBB.entrySet()){
			//We need to reset each time since we're changing the underlying string being matched on
			for (Matcher m = e.getValue().matcher(text); m.find(); m.reset(text)){
				ssb.replace(m.start(1), m.end(1), e.getKey());
				text.replace(m.start(1), m.end(1), e.getKey());
			}
		}
	}
}
