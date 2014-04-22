package what.whatandroid.comments;

import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import what.whatandroid.callbacks.ShowHiddenTagListener;

/**
 * Clickable span to open an image pop up dialog displaying the linked image. When clicked we
 * try to use the context's ShowHiddenTagListener to show the image, if this isn't implemented a
 * warning will be logged
 */
public class ShowImageSpan extends ClickableSpan {
	String url;

	ShowImageSpan(String url){
		super();
		this.url = url;
	}

	@Override
	public void onClick(View widget){
		try {
			ShowHiddenTagListener listener = (ShowHiddenTagListener)widget.getContext();
			if (listener != null){
				listener.showImage(url);
			}
		}
		catch (ClassCastException e){
			Log.w("WARN", "Attempt to show hidden text in context not implementing ShowHiddenTagListener");
		}
	}
}
