package what.forum;

/**
 * Holds all the quoted posts
 * 
 * 
 */
public class QuoteBuffer {
	private static final StringBuffer buffer = new StringBuffer();
	private static int id;

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

	public static void add(int id, String string) {
		if (QuoteBuffer.id != id) {
			clear();
		}
		QuoteBuffer.id = id;
		try {
			buffer.append(string);
			buffer.append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBuffer(int id) {
		if (QuoteBuffer.id != id) {
			clear();
		}
		QuoteBuffer.id = id;
		try {
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
