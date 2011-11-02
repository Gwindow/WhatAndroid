package what.torrents.artist;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import api.torrents.artist.Artist;

public class ArtistStatsActivity extends MyActivity implements OnCheckedChangeListener {
	private CheckBox notifications, bookmark;
	private Artist artist;
	private TextView numGroups, numTorrents, numSeeders, numLeechers, numSnatches;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artiststats);
		bookmark = (CheckBox) this.findViewById(R.id.bookmark);
		bookmark.setOnCheckedChangeListener(this);
		notifications = (CheckBox) this.findViewById(R.id.notify);
		if (MySoup.canNotifications() == false) {
			notifications.setVisibility(CheckBox.INVISIBLE);
		}
		notifications.setOnCheckedChangeListener(this);

		numGroups = (TextView) this.findViewById(R.id.groups);
		numTorrents = (TextView) this.findViewById(R.id.torrents);
		numSeeders = (TextView) this.findViewById(R.id.seeders);
		numLeechers = (TextView) this.findViewById(R.id.leechers);
		numSnatches = (TextView) this.findViewById(R.id.snatches);

		populateLayout();
	}

	private void populateLayout() {
		artist = ArtistTabActivity.getArtist();

		numGroups.setText("Groups :" + artist.getResponse().getStatistics().getNumGroups());
		numTorrents.setText("Torrents :" + artist.getResponse().getStatistics().getNumTorrents());
		numSeeders.setText("Seeders :" + artist.getResponse().getStatistics().getNumSeeders());
		numLeechers.setText("Leechers :" + artist.getResponse().getStatistics().getNumLeechers());
		numSnatches.setText("Snatches :" + artist.getResponse().getStatistics().getNumSnatches());

		notifications.setChecked(artist.getResponse().isNotificationsEnabled());
		bookmark.setChecked(artist.getResponse().isBookmarked());
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
