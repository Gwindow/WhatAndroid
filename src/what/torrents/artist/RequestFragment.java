package what.torrents.artist;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.requests.RequestActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.artist.Requests;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 2, 2012 11:24:20 AM
 */
public class RequestFragment extends SherlockFragment implements OnClickListener {
	private static final int REQUEST_TAG = 0;
	private LinearLayout scrollLayout;
	private List<Requests> requests;
	private MyScrollView scrollView;

	public RequestFragment() {
		super();
	}

	/**
	 * @param requests
	 */
	public RequestFragment(List<Requests> requests) {
		this.requests = requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate(view, inflater);
		return view;
	}

	private void populate(View view, LayoutInflater inflater) {
		for (int i = 0; i < requests.size(); i++) {
			TextView request_title = (TextView) inflater.inflate(R.layout.artist_request_title, null);
			String year = requests.get(i).getYear().intValue() != 0 ? requests.get(i).getYear() + " - " : "";
			request_title.setText(year + requests.get(i).getTitle());
			request_title.setOnClickListener(this);
			request_title.setTag(REQUEST_TAG);
			request_title.setId(requests.get(i).getRequestId().intValue());
			scrollLayout.addView(request_title);
		}
		if (requests.size() == 0) {
			TextView request_title = (TextView) inflater.inflate(R.layout.artist_request_title, null);
			request_title.setText("No Requests");
			scrollLayout.addView(request_title);
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REQUEST_TAG:
				openRequest(v.getId());
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	private void openRequest(int id) {
		Intent intent = new Intent(getSherlockActivity(), RequestActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.REQUEST_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * @param requests2
	 * @return
	 */
	public static SherlockFragment newInstance(List<Requests> requests) {
		return new RequestFragment(requests);
	}
}
