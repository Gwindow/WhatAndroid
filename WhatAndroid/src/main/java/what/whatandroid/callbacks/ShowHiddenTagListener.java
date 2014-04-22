package what.whatandroid.callbacks;

/**
 * Listener that will be called when some hidden text is clicked, either a hidden text
 * or mature text or images
 */
public interface ShowHiddenTagListener {
	/**
	 * Request that the hidden text be shown in a popup view
	 *
	 * @param title the title of the hidden text
	 * @param text  the hidden text to show, markup will be parsed
	 */
	public void showText(String title, String text);

	/**
	 * Request that the hidden image be shown in a popup view
	 *
	 * @param url the url of the image to show
	 */
	public void showImage(String url);
}
