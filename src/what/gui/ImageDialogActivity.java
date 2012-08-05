package what.gui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * @author Gwindow
 * @since Aug 4, 2012 1:04:19 PM
 */
public class ImageDialogActivity extends SherlockActivity {
	private ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.image_dialog);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setMaxHeight(metrics.heightPixels - 50);
		imageView.setMaxWidth(metrics.widthPixels - 50);
		imageView.setImageResource(R.drawable.loading);
		MyImageLoader imageLoader = new MyImageLoader(this, R.drawable.loading);
		Bundle bundle = getIntent().getExtras();
		imageLoader.displayImage(bundle.getString(BundleKeys.URL), imageView);
	}
}
