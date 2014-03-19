package what.whatandroid.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * Activity for performing Torrent, User or Request searches. The searching itself is handled
 * by the specific fragments
 */
public class SearchActivity extends ActionBarActivity
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, ViewTorrentCallbacks {
	/**
	 * Param to pass the search type desired and terms and tags if desired
	 */
	public final static String SEARCH = "what.whatandroid.SEARCH", TERMS = "what.whatandroid.SEARCH.TERMS",
		TAGS = "what.whatandroid.SEARCH.TAGS";
	/**
	 * The parameters to specify what we want to search for
	 */
	public final static String TORRENT = "TORRENT", ARTIST = "ARTIST", USER = "USER", REQUEST = "REQUEST";
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navDrawer;
	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);

		navDrawer = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
		title = getTitle();

		String type = getIntent().getStringExtra(SEARCH);
		String terms = getIntent().getStringExtra(TERMS);
		String tags = getIntent().getStringExtra(TAGS);

		Fragment fragment = null;
		if (type.equalsIgnoreCase(ARTIST)){
			setTitle("Artist Search");
			fragment = ArtistSearchFragment.newInstance(terms);
		}
		/*
		if (type.equalsIgnoreCase(REQUEST)){
			setTitle("Request Search");
			//set new request search fragment
		}
		else if (type.equalsIgnoreCase(USER)){
			setTitle("User Search");
			//set new user search fragment
		}
		*/
		else {
			setTitle("Torrent Search");
			fragment = TorrentSearchFragment.newInstance(terms, tags);
		}
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, fragment).commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))){
			//Launch AnnouncementsActivity viewing announcements
			//For now both just return to the announcements view
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			//Launch AnnouncementsActivity viewing blog posts
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			//Launch profile view activity
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		}
	}

	@Override
	public void setTitle(String t){
		title = t;
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(title);
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
		startActivity(intent);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
