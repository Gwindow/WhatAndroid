package what.inbox;

import what.gui.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

/**
 * @author Gwindow
 * @since May 26, 2012 8:18:10 PM
 */
public class ReplyDialog extends SlidingDrawer {
	enum Type {
		THREAD, MESSAGE;
	}

	private int id;
	private LinearLayout layout;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ReplyDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ReplyDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		createLayout();
	}

	private void createLayout() {
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService((Context.LAYOUT_INFLATER_SERVICE));
		layout = (LinearLayout) layoutInflater.inflate(R.layout.reply_dialog, null);
		addView(layout);
	}
}
