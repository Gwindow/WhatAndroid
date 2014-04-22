package what.whatandroid.comments;

import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import what.whatandroid.callbacks.ShowHiddenTagListener;

/**
 * Implements the behavior of the site's hidden/mature tags. When clicked we try to
 * get a ShowHiddenTagListener from the context and if it implements it we request
 * to display the hidden text
 */
public class HiddenTextSpan extends ClickableSpan {
	String title, text;

	public HiddenTextSpan(String title, String text){
		super();
		this.title = title;
		this.text = text;
	}

	@Override
	public void onClick(View widget){
		try {
			ShowHiddenTagListener listener = (ShowHiddenTagListener)widget.getContext();
			if (listener != null){
				listener.showText(title, text);
			}
		}
		catch (ClassCastException e){
			Log.w("WARN", "Attempt to show hidden text in context not implementing ShowHiddenTagListener");
		}
	}
}
