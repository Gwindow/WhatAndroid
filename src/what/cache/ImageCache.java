package what.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import api.util.Tuple;

public class ImageCache extends Activity {
	private static Map<Integer, Tuple<String, Bitmap>> imageMap;
	private static String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	private static OutputStream outStream = null;

	public static void init() {
		imageMap = new HashMap<Integer, Tuple<String, Bitmap>>();
	}

	private static void loadMap() {

	}

	public static void saveImage(int id, String url, Bitmap bitmap) {
		File file = new File(extStorageDirectory, String.valueOf(id));
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			imageMap.put(id, new Tuple<String, Bitmap>(url, bitmap));
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static boolean hasImage(int id, String url) {
		if (imageMap.containsKey(id)) {
			if (imageMap.get(id).getA().equalsIgnoreCase(url))
				return true;
		}
		return false;

	}

	public static Bitmap getImage(int id) {
		return imageMap.get(id).getB();
	}
}
