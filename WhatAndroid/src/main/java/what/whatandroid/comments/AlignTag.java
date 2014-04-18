package what.whatandroid.comments;

import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;

/**
 * Handles site alignment tags like [align=center] and returns the corresponding alignment span
 */
public class AlignTag implements ParameterizedTag {
	@Override
	public SpannableString getStyle(String param, String text){
		SpannableString styled = new SpannableString(text);
		if (param.equalsIgnoreCase("left")){
			styled.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else if (param.equalsIgnoreCase("center")){
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
