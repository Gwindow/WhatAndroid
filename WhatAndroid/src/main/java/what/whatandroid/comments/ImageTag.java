package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;

/**
 * Handles image tags on the site, instead of loading them inline we show
 * a clickable span to open a pop-up containing the image
 */
public class ImageTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableString styled;
		String url;
		if (param != null){
			url = param;
		}
		else {
			url = text;
		}
		styled = new SpannableString(url);
		styled.setSpan(new ShowImageSpan(url), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
