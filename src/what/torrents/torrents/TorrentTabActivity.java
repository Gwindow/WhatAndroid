package what.torrents.torrents;

import what.gui.MyTabActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
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
		super.setContentView(R.layout.tabs, true);
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
				tabHost.newTabSpec("torrent info").setIndicator("Info", res.getDrawable(R.drawable.tab_torrentinfo))
						.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, TorrentFormatsActivity.class);
		spec = tabHost.newTabSpec("formats").setIndicator("Formats", res.getDrawable(R.drawable.tab_download)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.table_header_dark);
		}
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

}
