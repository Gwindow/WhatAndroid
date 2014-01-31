package what.whatandroid.callbacks;

/**
 * An interface to allow fragments to set the title of the activity containing them
 */
public interface SetTitleCallback {
	/**
	 * Instruct the activity to set a new title
	 *
	 * @param t the new title
	 */
	public void setTitle(String t);
}
