package what.whatandroid.comments;


import android.text.Spannable;

/**
 * An interface for creating various types of parameterized tags. The object returned
 * must be some kind of Character or Paragraph style
 */
public interface ParameterizedTag {
	/**
	 * Parse the parameters for the tag and effected text and return the styled text
	 * for the tag
	 *
	 * @param param tag parameters
	 * @param text  text effected by the tag
	 * @return styled text resulting from applying the tag
	 */
	Spannable getStyle(String param, String text);
}
