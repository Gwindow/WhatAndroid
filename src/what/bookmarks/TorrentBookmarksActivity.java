package what.bookmarks;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.bookmarks.Bookmarks;
import api.bookmarks.Torrents;

public class TorrentBookmarksActivity extends MyActivity implements OnClickListener {
	private ScrollView scrollView;
	private ArrayList<TextView> bookmarksList;
	private LinearLayout scrollLayout;
	private Intent intent;
	private TextView title;
	private ProgressDialog dialog;
	private Bookmarks bookmarks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bookmarks, true);
	}

	@Override
	public void init() {
		bookmarksList = new ArrayList<TextView>();
	}

	@Override
	public void load() {
		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		title = (TextView) this.findViewById(R.id.title);
	}

	@Override
	public void prepare() {
		title.setText("Torrent Bookmarks");
		new LoadBookmarks().execute();
	}

	private void populateLayout() {
		if (bookmarks.hasTorrentBookmarks()) {
			List<Torrents> list = bookmarks.getResponse().getTorrents();
			for (int i = 0; i < list.size(); i++) {
				if ((i % 2) == 0) {
					bookmarksList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
				} else {
					bookmarksList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
				}
				bookmarksList.get(i).setText(list.get(i).getName() + " [" + list.get(i).getYear() + "]");
				bookmarksList.get(i).setId(i);
				bookmarksList.get(i).setOnClickListener(this);
				scrollLayout.addView(bookmarksList.get(i));
			}
		}
	}

	private void openBookmark(int i) {
		Bundle b = new Bundle();
		intent = new Intent(TorrentBookmarksActivity.this, what.torrents.torrents.TorrentTabActivity.class);
		b.putInt("torrentGroupId", bookmarks.getResponse().getTorrents().get(i).getId().intValue());
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < bookmarksList.size(); i++) {
			if (v.getId() == bookmarksList.get(i).getId()) {
				openBookmark(i);
			}
		}
	}

	@Override
	public void onDownGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);

	}

	private class LoadBookmarks extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(TorrentBookmarksActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			bookmarks = Bookmarks.loadTorrentBookmarks();
			return bookmarks.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(TorrentBookmarksActivity.this, "Could not load bookmarks", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

}
