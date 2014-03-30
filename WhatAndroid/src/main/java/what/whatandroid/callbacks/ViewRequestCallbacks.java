package what.whatandroid.callbacks;

/**
 * Callbacks to request that a torrent request be viewed
 */
public interface ViewRequestCallbacks {
	/**
	 * Request the activity to view a request with some id
	 *
	 * @param id id of request to view
	 */
	public void viewRequest(int id);
}
