package what.fragments;

import what.gui.MyActivity2;
import what.gui.MyTextView;
import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 1, 2012 6:06:03 PM
 */
public class DescriptionFragment extends SherlockFragment {
	private String description;

	public DescriptionFragment() {
		super();
	}

	public DescriptionFragment(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.description_fragment, container, false);
		MyTextView description_view = (MyTextView) view.findViewById(R.id.description);
		description_view.setClickable(false);
		if (description == null || description.length() == 0) {
			description = "No Description";
		} else {
			description_view.setText(description);
		}
		// Linkify.addLinks(description_view, Linkify.WEB_URLS);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(null);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @param body
	 * @return
	 */
	public static SherlockFragment newInstance(String body) {
		return new DescriptionFragment(body);
	}
}
