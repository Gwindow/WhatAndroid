package what.whatandroid.comments.tags;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;

/**
 * Handles site alignment tags like [align=center] and returns the corresponding alignment span
 */
public class AlignTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(text);
		if (param.charAt(0) == 'l'){
			styled.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else if (param.charAt(0) == 'c'){
			styled.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			styled.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return styled;
	}
}
