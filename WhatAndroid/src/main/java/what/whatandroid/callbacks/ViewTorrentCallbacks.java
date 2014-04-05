package what.whatandroid.callbacks;

/**
 * Callbacks for the fragments to launch an intent to view a torrent group or
 * torrent in a group
 */
public interface ViewTorrentCallbacks {
	/**
	 * Request to view a torrent group with some id
	 *
	 * @param id the id of the torrent group to view
	 */
	public void viewTorrentGroup(int id);

	/**
	 * Request to view a torrent within some group
	 *
	 * @param group   torrent group id
	 * @param torrent torrent id to view
	 */
	public void viewTorrent(int group, int torrent);
}
