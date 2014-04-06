package what.whatandroid.imgloader;

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
		//Does android have this one?
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
}
