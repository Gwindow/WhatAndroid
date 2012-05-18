package what.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author Gwindow
 * @since May 17, 2012 7:00:04 PM
 */
public class MyScrollView extends ScrollView {
	private Scrollable scrollable;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 */
	public MyScrollView(Context context) {
		super(context);
	}

	public void attachScrollable(Scrollable scrollable) {
		this.scrollable = scrollable;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		int bottomY = getBottomY();
		boolean hitBottom = getChildAt(getChildCount() - 1).getBottom() - bottomY == 0;
		if (hitBottom) {
			scrollable.scrolledToBottom();
			hitBottom = false;
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public int getBottomY() {
		return getHeight() + getScrollY();
	}
}
