package what.torrents.artist;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.artist.Artist;

public class RequestListActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private Artist artist;
	private Intent intent;
	private ArrayList<TextView> requestList = new ArrayList<TextView>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.request_list, true);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		populateLayout();
	}

	private void populateLayout() {
		artist = ArtistTabActivity.getArtist();
		if (artist.getStatus()) {
			for (int i = 0; i < artist.getResponse().getRequests().size(); i++) {
				if ((i % 2) == 0) {
					requestList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
				} else {
					requestList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
				}
				requestList.get(i).setText(
						artist.getResponse().getName() + " - " + artist.getResponse().getRequests().get(i).getTitle() + " ["
								+ artist.getResponse().getRequests().get(i).getYear() + "]");
				requestList.get(i).setId(i);
				requestList.get(i).setOnClickListener(this);
				scrollLayout.addView(requestList.get(i));
			}
		}
	}

	private void openRequest(int i) {
		Bundle b = new Bundle();
		intent = new Intent(RequestListActivity.this, what.requests.RequestTabActivity.class);
		b.putInt("requestId", artist.getResponse().getRequests().get(i).getRequestId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (requestList.size()); i++) {
			if (v.getId() == requestList.get(i).getId()) {
				openRequest(i);
			}
		}
	}
}
