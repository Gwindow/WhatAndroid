package what.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import api.soup.MySoup;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MyImageGetter implements ImageGetter {
	private static final double SCALE = 0.3;
	private static final String SMILEY = "static/common/smileys/";
	private double width, height;
	private MyImageLoader imageLoader;
	private int defaultImageResource;
	private Context context;
	private Drawable d;

	/**
	 * @param imageLoader
	 */
	/*
	 * public MyImageGetter(String url, Context context, MyImageLoader imageLoader, int defaultImageResource) {
	 * this.context = context; this.imageLoader = imageLoader; this.defaultImageResource = defaultImageResource; }
	 */
	/**
	 * @param imageLoader
	 */
	/*
	 * public MyImageGetter(Context context, MyImageLoader imageLoader) { this.context = context; this.imageLoader =
	 * imageLoader; }
	 */

	public MyImageGetter(Context context, int defaultImageResource) {
		this.context = context;
		imageLoader = new MyImageLoader(context);
		this.defaultImageResource = defaultImageResource;
	}

	public MyImageGetter(Context context) {
		this.context = context;
		imageLoader = new MyImageLoader(context);
	}

	@Override
	public Drawable getDrawable(String source) {
		if (source.startsWith(SMILEY)) {
			source = MySoup.getSite() + source;
		}

		d = context.getResources().getDrawable(R.drawable.site_down);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView view = (ImageView) inflater.inflate(R.layout.placeholder_imageview, null);
		view.setMaxHeight(MyActivity2.metrics.heightPixels - 20);
		view.setMaxWidth(MyActivity2.metrics.widthPixels - 20);

		imageLoader.displayImage(source, view, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(FailReason failReason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				d = new BitmapDrawable(context.getResources(), loadedImage);
			}

			@Override
			public void onLoadingCancelled() {
				// TODO Auto-generated method stub

			}

		});
		return d;

	}
}