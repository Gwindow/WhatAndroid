package what.forum.section;

import java.util.List;

import what.gui.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import api.forum.section.Threads;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * The Class ThreadFragment.
 * 
 * @author Gwindow
 * @since May 6, 2012 1:43:19 PM
 */
public class SectionFragment extends SherlockFragment {

	/** The Constant KEY_CONTENT. */
	private static final String KEY_CONTENT = "SectionFragment:Content";

	private View rootView;

	private int sectionId;
	private int fragmentId;

	public static Fragment newInstance(int fragmentId, int sectionId) {
		SectionFragment fragment = new SectionFragment();
		fragment.fragmentId = fragmentId;
		fragment.sectionId = sectionId;
		return fragment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			// mContent = savedInstanceState.getString(KEY_CONTENT);
		}
		rootView = inflater.inflate(R.layout.section, container, false);
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

	public void populate(List<Threads> list) {
		if (list != null && !list.isEmpty()) {

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putString(KEY_CONTENT, mContent);
	}

}
