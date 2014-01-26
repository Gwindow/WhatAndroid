package what.whatandroid.artist;

/**
 * Callbacks for the Artist fragment and torrent group list to set
 * the title or launch an intent to view a torrent group
 */
public interface ArtistCallbacks {
	/**
	 * Instruct the ArtistActivity to set a new title
	 *
	 * @param t the new title
	 */
	public void setTitle(String t);
}
