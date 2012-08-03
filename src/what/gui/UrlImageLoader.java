package what.gui;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 *
 */
public class UrlImageLoader {
	public static Bitmap loadBitmap(String s) throws IOException {
		URL url = new URL(s);
		return loadBitmap(url);
	}

	public static Bitmap loadBitmap(URL url) throws IOException {
		Bitmap b = BitmapFactory.decodeStream(new PatchInputStream(url.openStream()));
		return b;
	}
}
