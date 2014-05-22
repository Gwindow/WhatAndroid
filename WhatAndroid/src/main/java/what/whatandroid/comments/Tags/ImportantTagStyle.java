package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * Applies important text styling
 */
public class ImportantTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(text);
		styled.setSpan(new ForegroundColorSpan(0xffff4444), 0, styled.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
