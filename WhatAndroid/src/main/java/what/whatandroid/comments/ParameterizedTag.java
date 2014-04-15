package what.whatandroid.comments;


/**
 * An interface for creating various types of parameterized tags. The object returned
 * must be some kind of Character or Paragraph style
 */
public interface ParameterizedTag {
	/**
	 * Parse the parameters for the tag (and optionally the text effected) and
	 * return the appropriate tag
	 *
	 * @param param tag parameters
	 * @param text  text effected by the tag
	 * @return style to apply to the effected text
	 */
	Object getStyle(String param, String text);
}
