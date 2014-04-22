package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

/**
 * Handle site text size tags like [size={1,10}]
 */
public class SizeTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableString styled = new SpannableString(text);
		int size = Integer.parseInt(param);
		if (size > 0 && size < 11){
			styled.setSpan(new RelativeSizeSpan(1.f + (size - 2) * 0.2f), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return styled;
	}
}
