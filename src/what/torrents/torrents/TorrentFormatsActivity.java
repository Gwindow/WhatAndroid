package what.torrents.torrents;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import what.settings.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import api.torrents.torrents.TorrentGroup;
import api.util.CouldNotLoadException;

public class TorrentFormatsActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private TorrentGroup torrentGroup;
	private Intent intent;
	private TextView title;
	private ArrayList<TextView> torrentList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.formatlist, false);
	}

	@Override
	public void init() {
		torrentList = new ArrayList<TextView>();

	}

	@Override
	public void load() {
		title = (TextView) this.findViewById(R.id.title);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
	}

	@Override
	public void prepare() {
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
		String id = torrentGroup.getResponse().getTorrents().get(i).getId().toString();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		showDownloadDialog(id, url);
	}

	private void showDownloadDialog(final String torrentId, final String url) {
		AlertDialog alert = new AlertDialog.Builder(TorrentFormatsActivity.this).create();
		alert.setButton(AlertDialog.BUTTON1, "Download", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});
		alert.setButton(AlertDialog.BUTTON3, "Send to pyWA", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String host = Settings.getHostPreference();
				String port = Settings.getPortPreference();
				String password = Settings.getPasswordPreference();
				if ((host.length() > 0) && (port.length() > 0) && (password.length() > 0)) {
					String pyWaUrl = host + ":" + port + "/dl.pywa?pass=" + password + "&site=whatcd&id=" + torrentId;
					try {
						MySoup.scrapeOther(pyWaUrl);
						Toast.makeText(TorrentFormatsActivity.this, "Torrent sent", Toast.LENGTH_SHORT).show();
					} catch (CouldNotLoadException e) {
						Toast.makeText(TorrentFormatsActivity.this, "Could not send torrent", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(TorrentFormatsActivity.this, "Fill out pyWA information in Settings", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		alert.setButton(AlertDialog.BUTTON2, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert.setCancelable(true);
		alert.show();

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
