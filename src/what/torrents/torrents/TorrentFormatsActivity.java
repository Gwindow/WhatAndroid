package what.torrents.torrents;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.torrents.Torrents;

public class TorrentFormatsActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> sectionList = new ArrayList<TextView>();
	private ArrayList<TextView> torrentList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Torrents torrents;
	private Intent intent;
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formatlist);
		title = (TextView) this.findViewById(R.id.title);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		populateLayout();
	}

	private void populateLayout() {
		torrents = TorrentTabActivity.getTorrents();
		title.setText(torrents.getResponse().getGroup().getName());
		// TODO if statement
		for (int i = 0; i < torrents.getResponse().getTorrents().size(); i++) {
			if ((i % 2) == 0) {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			if (torrents.getResponse().getTorrents().get(i).isFreeTorrent()) {
				torrentList.get(i).setText(
						"Freeleech! " + torrents.getResponse().getGroup().getName() + " - "
								+ torrents.getResponse().getTorrents().get(i).getMediaFormatEncoding());
				torrentList.get(i).setTextColor(Color.YELLOW);
			} else {
				torrentList.get(i).setText(
						torrents.getResponse().getGroup().getName() + " - "
								+ torrents.getResponse().getTorrents().get(i).getMediaFormatEncoding());
			}
			torrentList.get(i).setId(i);
			torrentList.get(i).setOnClickListener(this);
			scrollLayout.addView(torrentList.get(i));
		}
	}

	/* private void downloadTorrent(int i) { String url = album.getFormatList().get(j).getDownloadLink(); if
	 * (!url.startsWith("http://") && !url.startsWith("https://")) { url = "http://" + url; } Intent i = new
	 * Intent(Intent.ACTION_VIEW); i.setData(Uri.parse(url)); startActivity(i);
	 * 
	 * } } */

	@Override
	public void onClick(View v) {
		// if ((v.getId() >= 0) && (artist.getResponse().getTorrentgroup().size() >= v.getId())) {
		// openTorrent(v.getId());
		// }
	}
}
