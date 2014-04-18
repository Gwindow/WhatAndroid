package what.whatandroid.comments;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;

/**
 * Returns the appropriate URLSpan for the url tag
 */
public class URLTag implements ParameterizedTag {
	@Override
	public SpannableString getStyle(String param, String text){
		SpannableString styled = new SpannableString(text);
		if (param != null && !param.isEmpty()){
			styled.setSpan(new URLSpan(param), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			styled.setSpan(new URLSpan(text), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return styled;
	}
}
