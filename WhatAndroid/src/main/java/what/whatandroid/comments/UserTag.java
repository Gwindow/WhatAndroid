package what.whatandroid.comments;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Handles site user tags, currently not used since we need to plan more about how we're going to handle
 * some of the path conflicts since android doesn't give us matching on the url parameters
 */
public class UserTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableString styled = new SpannableString(param);
		try {
			styled.setSpan(new URLSpan("https://what.cd/user.php?action=search&search=" + URLEncoder.encode(param, "UTF-8")),
				0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return styled;
	}
}
