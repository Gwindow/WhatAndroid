package what.torrents.torrents;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import api.torrents.torrents.Torrents;

public class TorrentStatsActivity extends MyActivity implements OnCheckedChangeListener {
	private CheckBox bookmark;
	private Torrents torrents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.torrentstats);
		bookmark = (CheckBox) this.findViewById(R.id.bookmark);
		bookmark.setOnCheckedChangeListener(this);
		populateLayout();
	}

	private void populateLayout() {
		torrents = TorrentTabActivity.getTorrents();
		bookmark.setChecked(torrents.getResponse().getGroup().isBookmarked());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == bookmark.getId()) {
			if (isChecked == true) {
				try {
					torrents.addBookmark();
				} catch (Exception e) {
					Toast.makeText(TorrentStatsActivity.this, "Could not add bookmark", Toast.LENGTH_LONG).show();
				}
			}
			if (isChecked == false) {
				try {
					torrents.removeBookmark();
				} catch (Exception e) {
					Toast.makeText(TorrentStatsActivity.this, "Could not remove bookmark", Toast.LENGTH_LONG).show();

				}
			}
		}
	}
}
