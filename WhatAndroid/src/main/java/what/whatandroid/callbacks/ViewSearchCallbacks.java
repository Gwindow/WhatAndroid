package what.whatandroid.callbacks;

/**
 * Callbacks to launch a search activity of some type with the desired terms
 */
public interface ViewSearchCallbacks {
	/**
	 * Start the desired search for the terms and tags passed
	 *
	 * @param type  type of search to start, use the values defined in SearchActivity
	 * @param terms terms to search for
	 * @param tags  tags for the search
	 */
	public void startSearch(int type, String terms, String tags);
}
