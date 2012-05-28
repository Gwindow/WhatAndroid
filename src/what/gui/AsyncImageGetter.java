package what.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Based from the example found here http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask
 */
public class AsyncImageGetter implements ImageGetter {
	private Context c;
	private View container;
	private int width, height;

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * 
	 * @param t
	 * @param c
	 */
	public AsyncImageGetter(View t, Context c, int width, int height) {
		this.c = c;
		this.container = t;
		this.width = width;
		this.height = height;
	}

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * 
	 * @param t
	 * @param c
	 */
	public AsyncImageGetter(View t, Context c) {
		this.c = c;
		this.container = t;
		this.width = width;
		this.height = height;
	}

	@Override
	public Drawable getDrawable(String source) {
		URLDrawable urlDrawable = new URLDrawable();

		// get the actual source
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

		asyncTask.execute(source);
		return urlDrawable;

	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;
		private DisplayMetrics displaymetrics;

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
			urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight());

			// change the reference of the current drawable to the result
			// from the HTTP call
			urlDrawable.drawable = result;

			// redraw the image by invalidating the container
			AsyncImageGetter.this.container.invalidate();
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