package what.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import api.util.Tuple;

public class ImageCache extends Activity {
	private static Map<Integer, Tuple<String, Bitmap>> imageMap;
	private static String extStorageDirectory = null;

	public static void init(Context mCtx) {
		imageMap = new WeakHashMap<Integer, Tuple<String, Bitmap>>();
		extStorageDirectory = mCtx.getExternalFilesDir(null).toString();
	}

	public static void saveImage(int id, String url, Bitmap bitmap) {
		OutputStream outStream = null;
		File file = new File(extStorageDirectory, String.valueOf(id));
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			imageMap.put(id, new Tuple<String, Bitmap>(url, bitmap));
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			// They're probably out of space on their SD is this happens...
			// Maybe we should notify them?
			e.printStackTrace();
		}
	}

	public static boolean hasImage(int id, String url) {
		if (imageMap.containsKey(id)) {
			if (imageMap.get(id).getA().equals(url))
				return true;
		}
		else {
			File file = new File(extStorageDirectory, String.valueOf(id));
			if (file.exists()) {
				try {
					FileInputStream in = new FileInputStream(file);
					Bitmap avatar = BitmapFactory.decodeStream(in);
					imageMap.put(id, new Tuple<String, Bitmap>(url, avatar));
					return true;
				} catch (FileNotFoundException e) {
					// Shouldn't occur since we already checked for its existence...
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;

	}

	public static Bitmap getImage(int id) {
		return imageMap.get(id).getB();
	}
}
