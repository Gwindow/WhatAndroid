package what.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;
import api.soup.MySoup;

/**
 * Based from the example found here http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask
 */
public class AsyncImageGetter implements ImageGetter {
	private static final double SCALE = 0.3;
	private static final String SMILEY = "static/common/smileys/";
	private Context c;
	private TextView container;
	private double width, height;

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * 
	 * @param t
	 * @param c
	 */
	public AsyncImageGetter(TextView t, Context c, double width, double height) {
		this.c = c;
		this.container = t;
		this.width = width * SCALE;
		this.height = height;
	}

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * 
	 * @param t
	 * @param c
	 */
	public AsyncImageGetter(TextView t, Context c) {
		this.c = c;
		this.container = t;
	}

	@Override
	public Drawable getDrawable(String source) {
		if (source.startsWith(SMILEY)) {
			source = MySoup.getSite() + source;
		}

		URLDrawable urlDrawable = new URLDrawable();

		// get the actual source
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

		asyncTask.execute(source);
		return urlDrawable;

	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable d) {
			this.urlDrawable = d;
		}

		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return fetchDrawable(source);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			// set the correct bound according to the result from HTTP call

			double resized_width = result.getIntrinsicWidth();
			double resized_height = result.getIntrinsicHeight();
			if (result.getIntrinsicWidth() > width) {
				resized_width = width;
				resized_height = width / (result.getIntrinsicWidth() / result.getIntrinsicHeight());
				Log.d("orig", String.valueOf(width));
			}
			urlDrawable.drawable =
					new BitmapDrawable(Bitmap.createScaledBitmap(((BitmapDrawable) result).getBitmap(), (int) resized_width,
							(int) resized_height, true));

			urlDrawable.setBounds(0, 0, (int) resized_width, (int) resized_height);

			Log.d("size", "width: " + result.getIntrinsicWidth() + ", resized width:  " + resized_width);

			// redraw the image by invalidating the container
			AsyncImageGetter.this.container.invalidate();

			// For ICS
			AsyncImageGetter.this.container.setHeight((int) (AsyncImageGetter.this.container.getHeight() + resized_height));

			// Pre ICS
			// AsyncImageGetter.this.textView.setEllipsize(null);
		}

		/***
		 * Get the Drawable from URL
		 * 
		 * @param urlString
		 * @return
		 */
		public Drawable fetchDrawable(String urlString) {
			try {
				InputStream is = fetch(urlString);
				Drawable drawable = Drawable.createFromStream(is, "src");
				drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
				return drawable;
			} catch (Exception e) {
				return c.getResources().getDrawable(R.drawable.ic_delete);
			}
		}

		private InputStream fetch(String urlString) throws MalformedURLException, IOException {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString);
			HttpResponse response = httpClient.execute(request);
			return response.getEntity().getContent();
		}
	}

	private class URLDrawable extends BitmapDrawable {
		// the drawable that you need to set, you could set the initial drawing
		// with the loading image if you need to
		protected Drawable drawable;

		@Override
		public void draw(Canvas canvas) {
			// override the draw to facilitate refresh function later
			if (drawable != null) {
				drawable.draw(canvas);
			}
		}
	}
}