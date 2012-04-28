package what.torrents.artist;

import what.gui.MyActivity;
import what.gui.R;
import what.settings.Settings;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import api.torrents.artist.Artist;

public class ArtistStatsActivity extends MyActivity implements OnCheckedChangeListener {
	private CheckBox notifications, bookmark;
	private Artist artist;
	private TextView numGroups, numTorrents, numSeeders, numLeechers, numSnatches;
	private ImageButton spotifyImage, lastfmImage;
	private TextView spotifyText, lastfmText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.artiststats);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		bookmark = (CheckBox) this.findViewById(R.id.bookmark);
		bookmark.setOnCheckedChangeListener(this);
		notifications = (CheckBox) this.findViewById(R.id.notify);
		notifications.setOnCheckedChangeListener(this);

		numGroups = (TextView) this.findViewById(R.id.groups);
		numTorrents = (TextView) this.findViewById(R.id.torrents);
		numSeeders = (TextView) this.findViewById(R.id.seeders);
		numLeechers = (TextView) this.findViewById(R.id.leechers);
		numSnatches = (TextView) this.findViewById(R.id.snatches);

		spotifyImage = (ImageButton) this.findViewById(R.id.spotify);
		lastfmImage = (ImageButton) this.findViewById(R.id.lastfm);
		spotifyText = (TextView) this.findViewById(R.id.spotifyText);
		lastfmText = (TextView) this.findViewById(R.id.lastfmText);

	}

	@Override
	public void prepare() {
		if (MySoup.canNotifications() == false) {
			notifications.setVisibility(CheckBox.INVISIBLE);
		}
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
		artist = ArtistTabActivity.getArtist();

		numGroups.setText("Groups: " + artist.getResponse().getStatistics().getNumGroups());
		numTorrents.setText("Torrents: " + artist.getResponse().getStatistics().getNumTorrents());
		numSeeders.setText("Seeders: " + artist.getResponse().getStatistics().getNumSeeders());
		numLeechers.setText("Leechers: " + artist.getResponse().getStatistics().getNumLeechers());
		numSnatches.setText("Snatches: " + artist.getResponse().getStatistics().getNumSnatches());

		notifications.setChecked(artist.getResponse().hasNotificationsEnabled());
		bookmark.setChecked(artist.getResponse().isBookmarked());
	}

	public void openSpotify(View v) {
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setAction(MediaStore.INTENT_ACTION_MEDIA_SEARCH);
			intent.setComponent(new ComponentName("com.spotify.mobile.android.ui", "com.spotify.mobile.android.ui.Launcher"));
			intent.putExtra(SearchManager.QUERY, "oscar peterson");
			startActivity(intent);
			finish();
		} catch (Exception e) {
			Toast.makeText(this, "Could not open Spotify, is it installed?", Toast.LENGTH_LONG).show();
		}

		/* try { String spotifyUri = artist.getSpotifyUrl(); Intent intent = new Intent(Intent.ACTION_VIEW,
		 * Uri.parse(spotifyUri)); startActivity(intent); } catch (Exception e) { Toast.makeText(this,
		 * "Could not open Spotify, is it installed?", Toast.LENGTH_LONG).show(); } */
	}

	public void openLastFM(View v) {
		Intent i = new Intent();
		i.setData(Uri.parse(artist.getLastFMUrl()));
		i.setAction("android.intent.action.VIEW");
		startActivity(i);
		finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == bookmark.getId()) {
			if (isChecked == true) {
				try {
					artist.addBookmark();
				} catch (Exception e) {
					Toast.makeText(ArtistStatsActivity.this, "Could not add bookmark", Toast.LENGTH_LONG).show();
				}
			}
			if (isChecked == false) {
				try {
					artist.removeBookmark();
				} catch (Exception e) {
					Toast.makeText(ArtistStatsActivity.this, "Could not remove bookmark", Toast.LENGTH_LONG).show();

				}
			}
		}
		if (buttonView.getId() == notifications.getId()) {
			if (isChecked == true) {
				try {
					artist.enableNotifications();
				} catch (Exception e) {
					Toast.makeText(ArtistStatsActivity.this, "Could not enable notifications", Toast.LENGTH_LONG).show();

				}
			}
			if (isChecked == false) {
				try {
					artist.disbaleNotifications();
				} catch (Exception e) {
					Toast.makeText(ArtistStatsActivity.this, "Could not disable notifications", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
