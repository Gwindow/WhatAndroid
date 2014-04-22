package what.whatandroid.comments;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;

/**
 * Handles site quote tags
 */
public class QuoteTag implements ParameterizedTag {
	@Override
	public Spannable getStyle(String param, String text){
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		String user = param;
		if (user != null){
			int nameEnd = user.indexOf('|');
			if (nameEnd != -1){
				user = user.substring(0, nameEnd);
			}
			user += " wrote:\n";
			ssb.append(user);
			ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		int end = ssb.length();
		ssb.append(text);
		ssb.setSpan(new QuoteSpan(0xff33b5e5), end, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}
}
