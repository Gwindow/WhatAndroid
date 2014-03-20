package what.whatandroid.callbacks;

/**
 * Callback for an activity to launch some user's profile view
 */
public interface ViewUserCallbacks {
	/**
	 * Request the activity to launch a profile activity viewing the user with some id
	 *
	 * @param id user id to view
	 */
	public void viewUser(int id);
}
