package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import api.soup.MySoup;

/**
 * Returns the appropriate URLSpan for the url tag
 */
public class URLTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(text);
		String url;
		if (param != null && param.length() != 0){
			url = param.toString();
		}
		else {
			url = text.toString();
		}
		//TODO: Is it safe to assume all urls without the http bit will be site urls?
		if (!url.startsWith("http")){
			url = MySoup.getSite() + url;
		}
		styled.setSpan(new URLSpan(url), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
