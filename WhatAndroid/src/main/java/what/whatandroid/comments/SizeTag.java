package what.whatandroid.comments;

import android.text.style.RelativeSizeSpan;

/**
 * Handle site text size tags like [size={1,10}]
 */
public class SizeTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		int size = Integer.parseInt(param);
		if (size > 0 && size < 11){
			return new RelativeSizeSpan(1.f + (size - 2) * 0.2f);
		}
		return new RelativeSizeSpan(1.f);
	}
}
