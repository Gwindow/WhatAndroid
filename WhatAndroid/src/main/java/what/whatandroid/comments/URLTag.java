package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import api.soup.MySoup;

/**
 * Returns the appropriate URLSpan for the url tag
 */
public class URLTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableString styled = new SpannableString(text);
		String url;
		if (param != null && !param.isEmpty()){
			url = param;
		}
		else {
			url = text;
		}
		//TODO: Is it safe to assume all urls without the http bit will be site urls?
		if (!url.startsWith("http")){
			url = MySoup.getSite() + url;
		}
		styled.setSpan(new URLSpan(url), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
