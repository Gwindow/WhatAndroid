package what.whatandroid.comments;

import android.text.style.URLSpan;

/**
 * Implements the behavior of site torrent tags, although we can't do the artist/group name
 * lookup like the site since that would require more api requests to look up the torrent and artist
 */
public class TorrentTag implements ParameterizedTag {
	@Override
	public Object getStyle(String param, String text){
		//The torrent tags can be the full url or just the group id
		if (!text.contains("what.cd")){
			return new URLSpan("https://what.cd/torrents.php?id=" + text);
		}
		return new URLSpan(text);
	}
}
