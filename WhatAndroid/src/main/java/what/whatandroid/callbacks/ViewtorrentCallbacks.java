package what.whatandroid.callbacks;

/**
 * Callbacks for the Artist fragment and torrent group list to set
 * the title or launch an intent to view a torrent group
 */
public interface ViewTorrentCallbacks {
	/**
	 * Have the Artist Activity launch a TorrentGroup activity so that we can view the
	 * details of one of the artists torrents
	 *
	 * @param id the id of the torrent group to view
	 */
	public void viewTorrentGroup(int id);
}
