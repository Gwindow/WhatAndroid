package what.whatandroid.callbacks;

/**
 * Listener that will be called when some hidden text is clicked, either a hidden text
 * or mature text
 */
public interface ShowHiddenTextListener {
	/**
	 * Request that the hidden text be shown in a popup view
	 *
	 * @param title the title of the hidden text
	 * @param text  the hidden text, will contain parsed mark-up
	 */
	public void showHidden(String title, CharSequence text);
}
