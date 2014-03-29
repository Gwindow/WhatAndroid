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

	public ImageLoadingListener(ProgressBar p){
		super();
		spinner = p;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view){
		spinner.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason){
		spinner.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
		spinner.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
	}
}
