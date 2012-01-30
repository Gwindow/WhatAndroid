package what.bookmarks;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.bookmarks.Artist;
import api.bookmarks.Bookmarks;

public class ArtistBookmarksActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> bookmarksList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Intent intent;
	private TextView title;
	private Bookmarks bookmarks = BookmarksTabActivity.getArtists();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bookmarks, true);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		title = (TextView) this.findViewById(R.id.title);
		title.setText("Artist Bookmarks");

		populateLayout();

	}

	private void populateLayout() {
		if (bookmarks.hasArtistBookmarks()) {
			List<Artist> list = bookmarks.getResponse().getArtists();
			for (int i = 0; i < list.size(); i++) {
				if ((i % 2) == 0) {
					bookmarksList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
				} else {
					bookmarksList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
				}
				bookmarksList.get(i).setText(list.get(i).getArtistName());
				bookmarksList.get(i).setId(i);
				bookmarksList.get(i).setOnClickListener(this);
				scrollLayout.addView(bookmarksList.get(i));
			}
		}
	}

	private void openBookmark(int i) {
		Bundle b = new Bundle();
		intent = new Intent(ArtistBookmarksActivity.this, what.torrents.artist.ArtistTabActivity.class);
		b.putInt("artistId", bookmarks.getResponse().getArtists().get(i).getArtistId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < bookmarksList.size(); i++) {
			if (v.getId() == bookmarksList.get(i).getId()) {
				openBookmark(i);
			}
		}
	}

}
