package what.user;

import what.gui.MyTabActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;
import api.user.User;

public class UserProfileTabActivity extends MyTabActivity {
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Intent intent; // Reusable Intent for each tab
	private static int userId;
	private static User user;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.tabs, true);
		getBundle();

		new LoadUser().execute();
	}

	private void createTabs() {
		res = getResources();
		tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, UserProfileInfoActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("info").setIndicator("Info", res.getDrawable(R.drawable.tab_torrentinfo)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, UserProfileStatsActivity.class);
		spec = tabHost.newTabSpec("stats").setIndicator("Stats", res.getDrawable(R.drawable.tab_details)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.table_header_dark);
		}
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		userId = b.getInt("userId");
	}

	private class LoadUser extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(UserProfileTabActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			user = User.userFromId(userId);
			return user.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				createTabs();
			} else {
				Toast.makeText(UserProfileTabActivity.this, "Could not load user profile", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

	public static User getUser() {
		return user;
	}

	public static int getUserId() {
		return userId;
	}

}
