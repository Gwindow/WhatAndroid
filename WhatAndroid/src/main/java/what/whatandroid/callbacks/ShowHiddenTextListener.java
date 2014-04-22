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
	 * @param text  the hidden text to show, markup will be parsed
	 */
	public void showHidden(String title, String text);
}
