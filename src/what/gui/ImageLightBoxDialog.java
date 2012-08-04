package what.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

public class ImageLightBoxDialog extends AlertDialog {
	private final String url;
	private final Context context;

	/**
	 * @param arg0
	 */
	public ImageLightBoxDialog(Context context, String url) {
		super(context);
		this.context = context;
		this.url = url;
		setCancelable(true);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		populate();
	}

	private void populate() {
		// LayoutInflater inflater = LayoutInflater.from(context);
		// LinearLayout f1 = (ImageView) alert.findViewById(android.R.id.body);
		// f1.addView(inflater.inflate(R.layout.dialog_view, f1, false));
		ImageView view = new ImageView(context);
		view.setImageResource(R.drawable.noartwork);
		view.setMaxHeight(MyActivity2.metrics.heightPixels - 100);
		view.setMaxWidth(MyActivity2.metrics.widthPixels - 50);
		MyImageLoader imageLoader = new MyImageLoader(context, R.drawable.noartwork);
		setView(view);
		show();
		imageLoader.displayImage(url, view);
	}
}
