package what.torrents.artist;

import java.util.List;

import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.artist.Requests;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 2, 2012 11:24:20 AM
 */
public class RequestFragment extends SherlockFragment implements OnClickListener {
	private static final int REQUEST_TAG = 0;
	private LinearLayout scrollLayout;
	private final List<Requests> requests;

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
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
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

	private void openRequest(int id) {
		// TODO finish
		/*
		 * Intent intent = new Intent(getActivity(), RequestActivity.class); Bundle bundle = new Bundle();
		 * bundle.putInt(BundleKeys.REQUEST_ID, id); intent.putExtras(bundle); startActivity(intent);
		 */
	}
}
