/**
 * 
 */
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

/**
 * @author Gwindow
 * 
 */
public class TorrentListActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> sectionList = new ArrayList<TextView>();
	private ArrayList<TextView> torrentList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Artist artist;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.torrentlist);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		populateLayout();
	}

	private void populateLayout() {
		artist = ArtistTabActivity.getArtist();
		// TODO if statement
		String currentReleaseType = "";
		int counter = 0;
		for (int i = 0; i < artist.getResponse().getTorrentgroup().size(); i++) {
			if (!currentReleaseType.equalsIgnoreCase(artist.getResponse().getTorrentgroup().get(i).getReleaseType())) {
				sectionList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_section_title, null));
				sectionList.get(counter).setText(artist.getResponse().getTorrentgroup().get(i).getReleaseType());
				scrollLayout.addView(sectionList.get(counter));
				currentReleaseType = artist.getResponse().getTorrentgroup().get(i).getReleaseType();
				counter++;
			}
			if ((i % 2) == 0) {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				torrentList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			torrentList.get(i).setText(
					artist.getResponse().getTorrentgroup().get(i).getGroupName() + " ["
							+ artist.getResponse().getTorrentgroup().get(i).getGroupYear() + "]");
			torrentList.get(i).setId(i);
			torrentList.get(i).setOnClickListener(this);
			scrollLayout.addView(torrentList.get(i));
		}
	}

	private void openTorrent(int i) {
		Bundle b = new Bundle();
		intent = new Intent(TorrentListActivity.this, what.torrents.torrents.TorrentTabActivity.class);
		b.putInt("torrentGroupId", (artist.getResponse().getTorrentgroup().get(i).getGroupId().intValue()));
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (torrentList.size()); i++) {
			if (v.getId() == torrentList.get(i).getId()) {
				openTorrent(i);
			}
		}
	}
}
