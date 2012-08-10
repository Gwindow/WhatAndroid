package what.fragments;

import what.gui.MyActivity2;
import what.gui.MyImageLoader;
import what.gui.R;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Gwindow
 * @since Jun 1, 2012 5:40:10 PM
 */
public class ArtFragment extends SherlockFragment {
	private ProgressBar progressBar;
	private ImageView artImageView;
	private static Bitmap artBitmap;
	private String url;
	private int resource;

	/**
	 * Instantiates a new art fragment.
	 * 
	 * @param title
	 *            the title
	 * @param torrentGroup
	 */
	public ArtFragment(String url) {
		this(url, R.drawable.noartwork);
	}

	public ArtFragment(String url, int resource) {
		this.url = url;
		this.resource = resource;
	}

	public ArtFragment() {
		super();
		this.resource = R.drawable.noartwork;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.art_fragment, container, false);
		artImageView = (ImageView) view.findViewById(R.id.art);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		MyImageLoader imageLoader = new MyImageLoader(getSherlockActivity());
		imageLoader.displayImage(url, artImageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted() {
			}

			@Override
			public void onLoadingFailed(FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				loadedImage = getRefelection(loadedImage);
				progressBar.setVisibility(View.GONE);
				artImageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled() {
			}
		});

		// artImageView.setImageBitmap(getRefelection(((BitmapDrawable) artImageView.getDrawable()).getBitmap()));

		/*
		 * if (artBitmap == null || artBitmap.isRecycled()) { new Load().execute(url); } else {
		 * progressBar.setVisibility(View.GONE); artImageView.setVisibility(View.VISIBLE);
		 * artImageView.setImageBitmap(artBitmap); }
		 */

		return view;
	}

	// Taken from http://androidsnips.blogspot.com/2010/08/showing-image-with-reflection-in.html
	private Bitmap getRefelection(Bitmap image) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 4;

		// Get you bit map from drawable folder
		Bitmap originalImage = image;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader =
				new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
						0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(null);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @param image
	 * @return
	 */
	public static SherlockFragment newInstance(String image) {
		return new ArtFragment(image);
	}

}
