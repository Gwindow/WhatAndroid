package what.torrents.torrents;

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
import api.torrents.torrents.TorrentGroup;

public class TorrentTabActivity extends MyTabActivity {
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Intent intent; // Reusable Intent for each tab
	private static int torrentGroupId;
	private static TorrentGroup torrentGroup;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);
		getBundle();

		new LoadTorrents().execute();
	}

	private void createTabs() {
		res = getResources();
		tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TorrentInfoActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec =
				tabHost.newTabSpec("torrent info").setIndicator("Info", res.getDrawable(R.drawable.torrentinfo_icon_dark))
						.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, TorrentFormatsActivity.class);
		spec =
				tabHost.newTabSpec("formats").setIndicator("Formats", res.getDrawable(R.drawable.download_icon_dark))
						.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		torrentGroupId = b.getInt("torrentGroupId");
	}

	private class LoadTorrents extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(TorrentTabActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			torrentGroup = TorrentGroup.torrentGroupFromId(torrentGroupId);
			return torrentGroup.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				createTabs();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(TorrentTabActivity.this, "Could not load torrents", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

	public static int getTorrentGroupId() {
		return torrentGroupId;
	}

	public static TorrentGroup getTorrentGroup() {
		return torrentGroup;
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

}
