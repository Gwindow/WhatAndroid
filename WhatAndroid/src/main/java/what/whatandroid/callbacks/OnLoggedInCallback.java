package what.whatandroid.callbacks;

/**
 * Callback for informing listeners that the user is logged in
 * and it's ok to start making API requests
 */
public interface OnLoggedInCallback {
	/**
	 * Function called when the user has logged in or has been confirmed to be logged in
	 */
	public void onLoggedIn();
}
