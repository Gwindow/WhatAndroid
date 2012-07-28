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
	private int pages = 1;

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
		int calc = getChildAt(getChildCount() - 1).getBottom() - bottomY;
		boolean hitBottom = calc == 0;
		if (hitBottom) {
			pages++;
			scrollable.scrolledToBottom();
			hitBottom = false;
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void scrollToBottom() {
		scrollTo(0, 0);
	}

	public void scrollToTop() {
		scrollTo(0, getBottomY());
	}

	public int getBottomY() {
		return getHeight() + getScrollY();
	}

	public int getPages() {
		return pages;
	}

}
