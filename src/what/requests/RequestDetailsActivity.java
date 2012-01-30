package what.requests;

import java.util.LinkedList;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RequestDetailsActivity extends MyActivity {
	private TextView requestTitle;
	private api.requests.Request request = RequestTabActivity.getRequest();
	private LinkedList<TextView> detailList = new LinkedList<TextView>();
	private LinearLayout scrollLayout;
	private int counter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.generic_list, true);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		requestTitle = (TextView) this.findViewById(R.id.title);
		populateLayout();
	}

	private void populateLayout() {
		requestTitle.setText(request.getResponse().getCategoryName() + " - " + request.getResponse().getTitle());
		if (request.getResponse().isFilled()) {
			addToList("Filled: Yes");
		} else {
			addToList("Filled: No");
		}
		addToList("Bounty: " + toGBString(request.getResponse().getTotalBounty().doubleValue()));
		addToList("Year: " + request.getResponse().getYear());
		addToList("Category: " + request.getResponse().getCategoryName());
		addToList("Catalogue Number: " + request.getResponse().getCatalogueNumber());
		addToList("Release Type: " + request.getResponse().getReleaseName());
		addToList("Bitrates: " + request.getResponse().getBitrateList());
		addToList("Formats: " + request.getResponse().getFormatList());
		addToList("Media: " + request.getResponse().getMediaList());
		if (request.getResponse().getLogCue().length() > 0) {
			addToList("Required Extras: " + request.getResponse().getLogCue());
		}
		addToList("Tags: " + request.getResponse().getTags().toString().replace("[", "").replace("]", ""));
		addToList("Created at: " + request.getResponse().getTimeAdded());
		addToList("Request by: " + request.getResponse().getRequestorName());
	}

	private void addToList(String s) {
		if ((counter % 2) == 0) {
			detailList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
		} else {
			detailList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
		}
		detailList.getLast().setText(s);
		scrollLayout.addView(detailList.getLast());
		counter++;
	}
}
