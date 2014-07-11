package what.whatandroid.callbacks;

/**
 * Interface to be implemented by the activity containing
 * a CommentsAdapter if it wants to receive callbacks when
 * the comment quote button is clicked
 */
public interface AddQuoteCallback {
	/**
	 * Called when the quote icon on a comment is pressed and
	 * is passed the quoted text
	 * @param quote quoted text
	 */
	public void quote(String quote);
}
