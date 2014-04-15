package what.whatandroid.comments;

import android.text.style.URLSpan;

/**
 * Returns the appropriate URLSpan for the url tag
 */
public class URLTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		System.out.println("parsing URL, param= '" + (param != null ? param : "null") + "', text= '" + text + "'");
		if (param != null && !param.isEmpty()){
			return new URLSpan(param);
		}
		return new URLSpan(text);
	}
}
