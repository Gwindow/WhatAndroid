package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;

/**
 * Implements the behavior of site torrent tags, although we can't do the artist/group name
 * lookup like the site since that would require more api requests to look up the torrent and artist
 */
public class TorrentTagStyle implements TagStyle {
	@Override
	public Spannable getStyle(CharSequence param, CharSequence text){
		SpannableString styled = new SpannableString(param);
		//The torrent tags can be the full url or just the group id
		String p = param.toString();
		if (!p.contains("what.cd")){
			styled.setSpan(new URLSpan("https://what.cd/torrents.php?id=" + param), 0, styled.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			styled.setSpan(new URLSpan(p), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return styled;
	}
}
