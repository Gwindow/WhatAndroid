package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Tag to implement the behavior of site artist tags, when clicked opens a link to the artist
 */
public class ArtistTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(param);
		try {
			styled.setSpan(new URLSpan("https://what.cd/artist.php?artistname=" +
				URLEncoder.encode(param.toString(), "UTF-8")), 0,
				styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return styled;
	}
}
