package what.whatandroid.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.imgloader.ImageLoadingListener;

/**
 * Basic dialog to display a full image, similar to how the site does when you
 * click on some album art
 */
public class ImageDialog extends DialogFragment implements View.OnClickListener {
	public static final String IMAGE_URL = "what.whatandroid.IMAGE_URL";
	private String imageUrl;

	/**
	 * Use this factory method to create an image dialog displaying the image at some url
	 * In typical usage of the app this image will already be cached by UIL so we won't
	 * actually need to do any downloading
	 */
	public static ImageDialog newInstance(String imgUrl){
		ImageDialog dialog = new ImageDialog();
		dialog.imageUrl = imgUrl;
		return dialog;
	}

	public ImageDialog(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//If we're loading from a saved state
		if (savedInstanceState != null && imageUrl == null){
			imageUrl = savedInstanceState.getString(IMAGE_URL);
		}
		setStyle(STYLE_NO_FRAME, R.style.ImageDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dialog_image, null);
		ImageView imageView = (ImageView)view.findViewById(R.id.image);
		ProgressBar spinner = (ProgressBar)view.findViewById(R.id.loading_indicator);
		ImageLoader.getInstance().displayImage(imageUrl, imageView, new ImageLoadingListener(spinner));
		imageView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(IMAGE_URL, imageUrl);
	}

	@Override
	public void onClick(View v){
		getDialog().dismiss();
	}
}
