package what.forum;

/**
 * Holds all the quoted posts
 * 
 * 
 */
public class QuoteBuffer {
	private final static StringBuffer buffer = new StringBuffer();

	public static void clear() {
		try {
			buffer.delete(0, buffer.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void add(String string) {
		try {
			buffer.append(string);
			buffer.append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBuffer() {
		try {
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
