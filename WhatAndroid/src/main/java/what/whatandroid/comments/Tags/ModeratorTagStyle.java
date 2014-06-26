package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tag for implementing styling of moderator edits on the site
 */
public class ModeratorTagStyle implements TagStyle {
	public static final Map<String, TagStyle> moderatorTags;

	static{
		moderatorTags = new TreeMap<String, TagStyle>(String.CASE_INSENSITIVE_ORDER);
		//Moderator tags edited out, ask Twinklebear
	}

	//The moderator name to be shown in the styled result
	private String moderator;

	public ModeratorTagStyle(String mod){
		moderator = mod;
	}

	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(moderator + " edit: " + text);
		styled.setSpan(new ForegroundColorSpan(0xffff4444), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
