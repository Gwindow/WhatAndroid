package what.whatandroid.comments.tags;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

/**
 * Applies Italic styling
 */
public class ItalicTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(text);
		styled.setSpan(new StyleSpan(Typeface.ITALIC), 0, styled.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
