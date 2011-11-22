package what.torrents.artist;

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
import api.torrents.artist.Artist;

public class ArtistTabActivity extends MyTabActivity {
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Intent intent; // Reusable Intent for each tab
	private ProgressDialog dialog;
	private static Artist artist;
	private static int artistId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top);

		getBundle();
		new LoadArtist().execute();

	}

	private void createTabs() {
		res = getResources();
		tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(ArtistTabActivity.this, ArtistActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("artist").setIndicator("Artist").setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(ArtistTabActivity.this, TorrentListActivity.class);
		spec = tabHost.newTabSpec("music").setIndicator("Music").setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(ArtistTabActivity.this, TorrentListActivity.class);
		spec = tabHost.newTabSpec("requests").setIndicator("Requests").setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	/**
	 * Get the bundle from the previous intent specifying the artist id
	 */
	private void getBundle() {
		try {
			Bundle b = this.getIntent().getExtras();
			// artistId = b.getString("artistId");
			// TODO remove
			artistId = 80927;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private class LoadArtist extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ArtistTabActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			artist = Artist.artistFromId(artistId);
			return artist.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				createTabs();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(ArtistTabActivity.this, "Could not load artist", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

	public static Artist getArtist() {
		return artist;
	}

	public static int getArtistId() {
		return artistId;
	}

}