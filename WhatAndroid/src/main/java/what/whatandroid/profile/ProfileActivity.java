package what.whatandroid.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import api.soup.MySoup;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;

public class ProfileActivity extends ActionBarActivity
	implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	/**
	 * Param to pass the user id to display to the activity
	 */
	public final static String USER_ID = "what.whatandroid.USER_ID";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navDrawer;
	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);

		navDrawer = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
		title = getTitle();

		//Will default to viewing our own profile
		ProfileFragment fragment = ProfileFragment.newInstance(getIntent().getIntExtra(USER_ID, MySoup.getUserId()));
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, fragment).commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (navDrawer == null) {
			return;
		}

		//Pass an argument to the activity telling it which to show?
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))) {
			//Launch AnnouncementsActivity viewing announcements
			//For now both just return to the announcements view
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))) {
			//Launch AnnouncementsActivity viewing blog posts
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			startActivity(intent);
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!navDrawer.isDrawerOpen()) {
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
