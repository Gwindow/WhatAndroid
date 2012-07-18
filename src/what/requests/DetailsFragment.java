package what.requests;

import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.cli.Utils;
import api.requests.Response;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jul 17, 2012 5:19:28 PM
 */
public class DetailsFragment extends SherlockFragment {

	private LinearLayout scrollLayout;
	private final Response response;

	/**
	 * @param response
	 */
	public DetailsFragment(Response response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populateStats(view);
		return view;
	}

	private void populateStats(View view) {
		if (response.isFilled()) {
			addToList("Filled", "Yes");
		} else {
			addToList("Filled", "No");
		}
		addToList("Bounty", Utils.toHumanReadableSize(response.getTotalBounty().longValue()));
		addToList("Year", response.getYear());
		addToList("Category", response.getCategoryName());
		addToList("Catalogue Number", response.getCatalogueNumber());
		addToList("Release Type", response.getReleaseName());
		addToList("Bitrates", response.getBitrateList());
		addToList("Formats", response.getFormatList());
		addToList("Media", response.getMediaList());
		if (response.getLogCue().length() > 0) {
			addToList("Required Extras", response.getLogCue());
		}
		addToList("Tags", response.getTags().toString().replace("[", "").replace("]", ""));
		addToList("Created at", response.getTimeAdded());
		addToList("Request by", response.getRequestorName());
	}

	private void addToList(String type, Object value) {
		TextView title = (TextView) View.inflate(getSherlockActivity(), R.layout.user_stats_title, null);
		if (value != null) {
			String displayed_string = type + ": " + value;
			title.setText(displayed_string);
			scrollLayout.addView(title);
		}
	}

}
