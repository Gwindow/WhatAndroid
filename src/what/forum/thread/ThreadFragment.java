package what.forum.thread;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * The Class ThreadFragment.
 * 
 * @author Gwindow
 * @since May 6, 2012 1:43:19 PM
 */
public class ThreadFragment extends SherlockFragment {

	/** The Constant KEY_CONTENT. */
	private static final String KEY_CONTENT = "ThreadFragment:Content";

	/** The m content. */
	private String mContent = "???";

	private int threadId;
	private int threadPage;
	private int postId;

	private int fragmentId;

	/**
	 * @param threadId
	 * @param threadPage
	 * @param postId
	 */
	public ThreadFragment(int threadId, int threadPage) {
		this.threadId = threadId;
		this.threadPage = threadPage;
	}

	public ThreadFragment(int fragmentId) {
		this.fragmentId = fragmentId;
	}

	public static Fragment newInstance(int fragmentId) {
		ThreadFragment fragment = new ThreadFragment(fragmentId);
		fragment.mContent = String.valueOf(fragmentId);
		return fragment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
		this.getSherlockActivity().getActionBar().setTitle("this is a thread");
		TextView text = new TextView(getActivity());
		text.setGravity(Gravity.CENTER);
		text.setText(mContent);
		text.setTextSize(20 * getResources().getDisplayMetrics().density);
		text.setPadding(20, 20, 20, 20);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		layout.setGravity(Gravity.CENTER);
		layout.addView(text);

		return layout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

}
