package what.gui;

import android.util.DisplayMetrics;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jul 28, 2012 11:24:34 AM
 */
public class MySherlockFragment extends SherlockFragment {

	private DisplayMetrics metrics;

	public MySherlockFragment() {
		metrics = new DisplayMetrics();
		getSherlockActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}

	/**
	 * @param scrollView
	 */
	public void homeIconJump(MyScrollView scrollView) {
		if (scrollView.getTop() <= 0) {
			getSherlockActivity().finish();
		} else {
			scrollView.scrollToTop();
		}
	}

	/**
	 * @return the metrics
	 */
	public DisplayMetrics getMetrics() {
		return metrics;
	}

}
