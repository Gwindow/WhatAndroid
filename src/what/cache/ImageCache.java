package what.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache extends Activity {
	private static Map<Integer, SoftReference<Bitmap>> imageMap;
	private static Map<Integer, String> urlMap;
	private static String extStorageDirectory = null;

	@SuppressWarnings("unchecked") // readObject() is safe here, honest.
	public static void init(Context mCtx) {
		imageMap = new HashMap<Integer, SoftReference<Bitmap>>();
		extStorageDirectory = mCtx.getExternalCacheDir().toString();
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(
							new File(extStorageDirectory, "urlmap")
							)
					);
			urlMap = (HashMap<Integer, String>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			urlMap = new HashMap<Integer, String>();
		}
	}

	public static void syncMap() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(
							new File(extStorageDirectory, "urlmap")
							)
					);
			oos.writeObject(urlMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveImage(int id, String url, Bitmap bitmap) {
		OutputStream outStream = null;
		File file = new File(extStorageDirectory, String.valueOf(id));
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			imageMap.put(id, new SoftReference<Bitmap>(bitmap));
			urlMap.put(id, url);
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
			if (urlMap.containsKey(id) && urlMap.get(id).equals(url))
				return true;
		}
		else {
			File file = new File(extStorageDirectory, String.valueOf(id));
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}

	public static Bitmap getImage(int id) {
		Bitmap avatar = null;
		if (imageMap.containsKey(id)) {
			avatar = imageMap.get(id).get();
		}
		if (avatar == null) {
			try {
				avatar = loadImage(id);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				// Shouldn't occur since we already checked for its existence...
			}
		}
		return avatar;
	}

	public static Bitmap loadImage(int id) throws FileNotFoundException {
			File file = new File(extStorageDirectory, String.valueOf(id));
			FileInputStream in = new FileInputStream(file);
			Bitmap avatar = BitmapFactory.decodeStream(in);
			imageMap.put(id, new SoftReference<Bitmap>(avatar));
			return avatar;
	}
}
