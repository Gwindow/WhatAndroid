package what.torrents;

import what.gui.R;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 1, 2012 6:06:03 PM
 */
public class DescriptionFragment extends SherlockFragment {
	private String description;

	public DescriptionFragment(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.description_fragment, container, false);
		TextView description_view = (TextView) view.findViewById(R.id.description);
		description_view.setText(Html.fromHtml(description));
		return view;
	}
}
