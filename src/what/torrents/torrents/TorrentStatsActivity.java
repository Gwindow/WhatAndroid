package what.torrents.torrents;

import what.gui.MyActivity;
import what.gui.R;
import what.settings.Settings;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import api.torrents.torrents.TorrentGroup;

public class TorrentStatsActivity extends MyActivity implements OnCheckedChangeListener {
	private CheckBox bookmark;
	private TorrentGroup torrentGroup;
	private ImageButton spotifyImage, lastfmImage;
	private TextView spotifyText, lastfmText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrentstats);
	}

	@Override
	public void init() {

	}

	@Override
	public void load() {
		bookmark = (CheckBox) this.findViewById(R.id.bookmark);
		spotifyImage = (ImageButton) this.findViewById(R.id.spotify);
		lastfmImage = (ImageButton) this.findViewById(R.id.lastfm);
		spotifyText = (TextView) this.findViewById(R.id.spotifyText);
		lastfmText = (TextView) this.findViewById(R.id.lastfmText);

		// TODO see why bookmarks arent working?
		bookmark.setVisibility(CheckBox.GONE);
		bookmark.setOnCheckedChangeListener(this);
	}

	@Override
	public void prepare() {
		if (Settings.getSpotifyButton() == false) {
			spotifyImage.setVisibility(View.GONE);
			spotifyText.setVisibility(View.GONE);
		}

		if (Settings.getLastfmButton() == false) {
			lastfmImage.setVisibility(View.GONE);
			lastfmText.setVisibility(View.GONE);
		}

		populateLayout();

	}

	private void populateLayout() {
		torrentGroup = TorrentTabActivity.getTorrentGroup();
		// bookmark.setChecked(torrentGroup.getResponse().getGroup()
	}

	public void openSpotify(View v) {
		try {
			String spotifyUri = torrentGroup.getSpotifyUrl();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri));
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "Could not open Spotify, is it installed?", Toast.LENGTH_LONG).show();
		}
	}

	public void openLastFM(View v) {
		Intent i = new Intent();
		i.setData(Uri.parse(torrentGroup.getLastFMUrl()));
		i.setAction("android.intent.action.VIEW");
		startActivity(i);
		finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == bookmark.getId()) {
			if (isChecked == true) {
				try {
					// torrentGroup.addBookmark();
				} catch (Exception e) {
					Toast.makeText(TorrentStatsActivity.this, "Could not add bookmark", Toast.LENGTH_LONG).show();
				}
			}
			if (isChecked == false) {
				try {
					// torrentGroup.removeBookmark();
				} catch (Exception e) {
					Toast.makeText(TorrentStatsActivity.this, "Could not remove bookmark", Toast.LENGTH_LONG).show();

				}
			}
		}
	}
}
