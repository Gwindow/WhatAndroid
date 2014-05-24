package what.whatandroid.callbacks;

/**
 * Callbacks for viewing forums or threads within forums
 */
public interface ViewForumCallbacks {
	/**
	 * Request to open a view of some forum
	 *
	 * @param id id of forum to view
	 */
	public void viewForum(int id);

	/**
	 * Request to open a view of some thread
	 *
	 * @param id thread if to view
	 */
	public void viewThread(int id);
}
