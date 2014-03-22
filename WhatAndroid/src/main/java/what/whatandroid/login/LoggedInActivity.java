package what.whatandroid.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import api.soup.MySoup;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.settings.SettingsActivity;

/**
 * Parent activity for ones that require the user to be logged in. If the user
 * enters a LoggedInActivity it will try to load their saved cookie, or if no
 * cookie is found will kick them to the login activity.
 */
public abstract class LoggedInActivity extends ActionBarActivity
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, SetTitleCallback, OnLoggedInCallback {
	protected NavigationDrawerFragment navDrawer;
	/**
	 * Used to store the last screen title, for use in restoreActionBar
	 */
	private CharSequence title;
	/**
	 * Prevent calling the onLoggedIn method multiple times
	 */
	private boolean calledLogin = false;

	@Override
	protected void onResume(){
		super.onResume();
		checkLoggedIn();
	}

	/**
	 * Setup the nav drawer and title, should be called after setting content view
	 */
	protected void setupNavDrawer(){
		navDrawer = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
		title = getTitle();
	}

	/**
	 * Check if the user is logged in, if they aren't kick them to the login activity
	 */
	private void checkLoggedIn(){
		if (!MySoup.isLoggedIn()){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_REQUEST, true);
			startActivityForResult(intent, 0);
		}
		//If we're already logged in tell the activity it's ok to start loading if we haven't already told them
		else if (!calledLogin){
			calledLogin = true;
			onLoggedIn();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == Activity.RESULT_OK){
			//Alert the activity it's ok to start loading if we haven't already
			if (!calledLogin){
				calledLogin = true;
				onLoggedIn();
			}
		}
	}

	@Override
	protected void onPause(){
		super.onPause();
		calledLogin = false;
	}

	@Override
	public void setTitle(String t){
		title = t;
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(title);
	}

	public void restoreActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (!navDrawer.isDrawerOpen()){
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.what_android, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			case R.id.action_logout:
				new LogoutTask().execute();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Async task for logging out the user
	 */
	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute(){
			dialog = new ProgressDialog(LoggedInActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Logging out...");
			dialog.show();
		}

		/**
		 * Once we've logged out clear the saved cookie, name and password and head to the home screen
		 */
		@Override
		protected void onPostExecute(Boolean status){
			dialog.dismiss();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoggedInActivity.this);
			preferences.edit()
				.remove(SettingsActivity.USER_COOKIE)
				.remove(SettingsActivity.USER_NAME)
				.remove(SettingsActivity.USER_PASSWORD)
				.commit();

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		@Override
		protected Boolean doInBackground(Void... params){
			Boolean status = false;
			try {
				status = MySoup.logout("logout.php");
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return status;
		}
	}
}
