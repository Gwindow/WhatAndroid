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

		MyImageLoader imageLoader = new MyImageLoader(getSherlockActivity(), R.drawable.noartwork);

		imageLoader.displayImage(url, artImageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				artImageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		});
		return view;
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

	public static SherlockFragment newInstance(String image) {
		return new ArtFragment(image);
	}

}
