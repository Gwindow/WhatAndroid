package what.whatandroid.imgloader;

import android.graphics.Bitmap;
import android.view.View;
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
	private final LoadFailTracker failTracker;

	/**
	 * Create the loading listener to update the desired spinner and image container
	 *
	 * @param spinner     spinner to show/hide for loading
	 * @param container   optional container to show/hide depending on loading status (can be null)
	 * @param failTracker load fail tracker to add this image url to if we fail loading (can be null)
	 */
	public ImageLoadingListener(ProgressBar spinner, View container, LoadFailTracker failTracker){
		super();
		this.spinner = spinner;
		this.container = container;
		this.failTracker = failTracker;
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
		if (container != null){
			container.setVisibility(View.GONE);
		}
		if (failTracker != null){
			failTracker.addFailed(imageUri);
		}
		spinner.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
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
