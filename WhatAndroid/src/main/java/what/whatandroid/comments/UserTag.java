package what.whatandroid.comments;

import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Handles site user tags, currently not used since we need to plan more about how we're going to handle
 * some of the path conflicts since android doesn't give us matching on the url parameters
 */
public class UserTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		try {
			return new URLSpan("https://what.cd/user.php?action=search&search=" + URLEncoder.encode(text, "UTF-8"));
		}
		catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return new UnderlineSpan();
	}
}
