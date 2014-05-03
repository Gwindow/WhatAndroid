package what.whatandroid.bookmarks;

/**
 * Callback to indicate to the fragment that the bookmarks have
 * changed and should be re-loaded the next time we load them,
 * instead of re-using the previously loaded bookmarks
 */
public interface BookmarksChangedListener {
	/**
	 * Tell the listener the bookmarks have changed
	 */
	public void bookmarksChanged();
}
