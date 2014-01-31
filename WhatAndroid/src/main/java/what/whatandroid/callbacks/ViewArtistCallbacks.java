package what.whatandroid.callbacks;

/**
 * Callbacks for the torrent group fragment to change the title or transition to the
 * an artist activity viewing an artist who worked on the album
 */
public interface ViewArtistCallbacks extends SetTitleCallback {
	/**
	 * Launch an artists activity viewing the artist
	 *
	 * @param id artist id to view
	 */
	public void viewArtist(int id);
}
