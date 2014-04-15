package what.whatandroid.comments;

import android.text.Layout;
import android.text.style.AlignmentSpan;

/**
 * Handles site alignment tags like [align=center] and returns the corresponding alignment span
 */
public class AlignTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		if (param.equalsIgnoreCase("left")){
			return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
		}
		if (param.equalsIgnoreCase("center")){
			return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
		}
		return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
	}
}
