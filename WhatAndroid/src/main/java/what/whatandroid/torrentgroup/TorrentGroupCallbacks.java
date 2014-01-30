package what.whatandroid.torrentgroup;

import what.whatandroid.utility.SetTitleCallback;

/**
 * Callbacks for the torrent group fragment to change the title or transition to the
 * an artist activity viewing an artist who worked on the album
 */
public interface TorrentGroupCallbacks extends SetTitleCallback {
	/**
	 * Launch an artists activity viewing the artist
	 *
	 * @param id artist id to view
	 */
	public void viewArtist(int id);
}
