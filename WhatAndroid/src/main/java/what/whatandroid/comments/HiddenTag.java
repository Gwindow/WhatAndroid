package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

/**
 * Handles site hidden and mature tags
 */
public class HiddenTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		if (param != null){
			ssb.append(param).append(": show");
		}
		else {
			ssb.append("Show hidden text");
		}
		ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new HiddenTextSpan(param, text), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}
}
