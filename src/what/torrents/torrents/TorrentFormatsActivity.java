package what.torrents.torrents;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.torrents.TorrentGroup;

public class TorrentFormatsActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> torrentList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private TorrentGroup torrentGroup;
	private Intent intent;
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.formatlist, false);
		title = (TextView) this.findViewById(R.id.title);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		populateLayout();
	}

	private void populateLayout() {
		torrentGroup = TorrentTabActivity.getTorrentGroup();
		title.setText(torrentGroup.getResponse().getGroup().getName());
		// TODO if statement
		for (int i = 0; i < torrentGroup.getResponse().getTorrents().size(); i++) {
			if ((i % 2) == 0) {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			if (torrentGroup.getResponse().getTorrents().get(i).isFreeTorrent()) {
				torrentList.get(i).setText(
						"Freeleech! " + torrentGroup.getResponse().getGroup().getName() + " - "
								+ torrentGroup.getResponse().getTorrents().get(i).getMediaFormatEncoding());
				torrentList.get(i).setTextColor(Color.YELLOW);
			} else {
				torrentList.get(i).setText(
						torrentGroup.getResponse().getGroup().getName() + " - "
								+ torrentGroup.getResponse().getTorrents().get(i).getMediaFormatEncoding());
			}
			torrentList.get(i).setId(i);
			torrentList.get(i).setOnClickListener(this);
			scrollLayout.addView(torrentList.get(i));
		}
	}

	private void downloadTorrent(int i) {
		String url = torrentGroup.getResponse().getTorrents().get(i).getDownloadLink();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);

	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (torrentList.size()); i++) {
			if (v.getId() == torrentList.get(i).getId()) {
				downloadTorrent(i);
			}
		}
	}
}
