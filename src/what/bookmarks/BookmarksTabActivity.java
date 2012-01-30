package what.bookmarks;

import what.gui.MyTabActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TabHost;
import android.widget.Toast;
import api.bookmarks.Bookmarks;

public class BookmarksTabActivity extends MyTabActivity {
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Intent intent; // Reusable Intent for each tab
	private ProgressDialog dialog;
	private static Bookmarks torrents;
	private static Bookmarks artists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.tabs, true);
		// TODO reenable
		// new LoadBookmarks().execute();

	}

	private void createTabs() {
		res = getResources();
		tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(BookmarksTabActivity.this, TorrentBookmarksActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec =
				tabHost.newTabSpec("torrents").setIndicator("Torrents", res.getDrawable(R.drawable.music_icon_dark))
						.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(BookmarksTabActivity.this, ArtistBookmarksActivity.class);
		spec =
				tabHost.newTabSpec("artists").setIndicator("Artists", res.getDrawable(R.drawable.artist_icon_dark))
						.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {
				tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private class LoadBookmarks extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(BookmarksTabActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			torrents = Bookmarks.initTorrentBookmarks();
			artists = Bookmarks.initArtistBookmarks();
			if ((artists.getStatus() == false) || (torrents.getStatus() == false))
				return false;
			return true;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				createTabs();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(BookmarksTabActivity.this, "Could not load bookmarks", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

	public static Bookmarks getTorrents() {
		return torrents;
	}

	public static Bookmarks getArtists() {
		return artists;
	}
}
