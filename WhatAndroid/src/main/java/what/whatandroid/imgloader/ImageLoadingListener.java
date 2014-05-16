package what.whatandroid.imgloader;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * An image loading listener that will display the passed progress indicator
 * when loading starts and hide it when loading finishes
 */
public class ImageLoadingListener extends SimpleImageLoadingListener {
	private final ProgressBar spinner;
	private final View container;
	private final ImageLoadFailTracker failTracker;
	private final Integer failImage;

	/**
	 * Create an image loading listener to hide the spinner when loading finishes
	 */
	public ImageLoadingListener(ProgressBar spinner){
		super();
		this.spinner = spinner;
		this.container = null;
		this.failTracker = null;
		this.failImage = null;
	}

	/**
	 * Create an image loading listener to update the spinner and art container when
	 * loading has finished
	 */
	public ImageLoadingListener(ProgressBar spinner, View container){
		super();
		this.spinner = spinner;
		this.container = container;
		this.failTracker = null;
		this.failImage = null;
	}

	/**
	 * Create the loading listener to update the desired spinner and image container and record
	 * images that failed to load
	 *
	 * @param spinner     spinner to show/hide for loading
	 * @param container   optional container to show/hide depending on loading status
	 * @param failTracker load fail tracker to add this image url to if we fail loading
	 */
	public ImageLoadingListener(ProgressBar spinner, View container, ImageLoadFailTracker failTracker){
		super();
		this.spinner = spinner;
		this.container = container;
		this.failTracker = failTracker;
		this.failImage = null;
	}

	/**
	 * Create the loading listener to update the desired spinner and image container and record
	 * images that failed to load. Will also display the fail image when loading has failed
	 *
	 * @param spinner     spinner to show/hide for loading
	 * @param container   optional container to show/hide depending on loading status
	 * @param failTracker load fail tracker to add this image url to if we fail loading
	 * @param failImage   image to display when loading fails
	 */
	public ImageLoadingListener(ProgressBar spinner, View container, ImageLoadFailTracker failTracker, Integer failImage){
		super();
		this.spinner = spinner;
		this.container = container;
		this.failTracker = failTracker;
		this.failImage = failImage;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view){
		if (container != null){
			container.setVisibility(View.VISIBLE);
		}
		spinner.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason){
		spinner.setVisibility(View.GONE);
		if (failTracker != null){
			failTracker.addFailed(imageUri);
		}
		if (failImage != null){
			((ImageView)view).setImageResource(failImage);
			view.setVisibility(View.VISIBLE);
			if (container != null){
				container.setVisibility(View.VISIBLE);
			}
		}
		else {
			view.setVisibility(View.GONE);
			if (container != null){
				container.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
		if (container != null){
			container.setVisibility(View.VISIBLE);
		}
		spinner.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
	}
}
