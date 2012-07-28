package what.gui;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jul 28, 2012 11:24:34 AM
 */
public class MySherlockFragment extends SherlockFragment {
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

}
