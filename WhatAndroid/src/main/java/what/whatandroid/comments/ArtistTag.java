package what.whatandroid.comments;

import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Tag to implement the behavior of site artist tags, when clicked opens a link to the artist
 */
public class ArtistTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		System.out.println(text);
		try {
			return new URLSpan("https://what.cd/artist.php?artistname=" + URLEncoder.encode(text, "UTF-8"));
		}
		catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return new UnderlineSpan();
	}
}
