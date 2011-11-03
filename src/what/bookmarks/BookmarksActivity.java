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
import android.widget.TextView;
import android.widget.Toast;
import api.bookmarks.Bookmarks;
import api.bookmarks.BookmarksList;

public class BookmarksActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> bookmarksList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Bookmarks bookmarks;
	private Intent intent;
	private ProgressDialog dialog;
	private int counter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		new LoadBookmarks().execute();
	}

	private void populateLayout() {
		List<BookmarksList> list = bookmarks.getResponse().getBookmarksList();
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
			counter++;
		}
	}

	private void openBookmark(int i) {
		Bundle b = new Bundle();
		intent = new Intent(BookmarksActivity.this, what.torrents.torrents.TorrentTabActivity.class);
		b.putString("torrentGroupId", bookmarks.getResponse().getBookmarksList().get(i).getId());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		if ((v.getId() >= 0) && (counter >= v.getId())) {
			openBookmark(v.getId());
		}
	}

	private class LoadBookmarks extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(BookmarksActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			bookmarks = Bookmarks.init();
			return bookmarks.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			if (status == false) {
				Toast.makeText(BookmarksActivity.this, "Could not load bookmarks", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
