package what.whatandroid.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import api.son.MySon;
import api.soup.MySoup;
import api.user.Profile;
import api.user.recent.UserRecents;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

public class ProfileActivity extends LoggedInActivity
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, ViewTorrentCallbacks {
	/**
	 * Param to pass the user id to display to the activity
	 */
	public static final String USER_ID = "what.whatandroid.USER_ID";
	/**
	 * Keys for reading saved profile information from the bundle
	 */
	private static final String PROFILE = "what.whatandroid.PROFILE", RECENTS = "what.whatandroid.RECENTS";
	private ProfileFragment profileFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		//Check if our saved state matches the user id we want to view (ie. the phone orientation changed)
		int id = getIntent().getIntExtra(USER_ID, MySoup.getUserId());
		if (savedInstanceState != null && savedInstanceState.getInt(USER_ID) == id){
			Profile p = (Profile)MySon.toObjectFromString(savedInstanceState.getString(PROFILE), Profile.class);
			UserRecents r = (UserRecents)MySon.toObjectFromString(savedInstanceState.getString(RECENTS), UserRecents.class);
			profileFragment = ProfileFragment.newInstance(id, p, r);
		}
		else {
			profileFragment = ProfileFragment.newInstance(id);
		}
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, profileFragment).commit();
	}

	@Override
	public void onLoggedIn(){
		//If we were trying to view our own profile the user id could be invalid if we weren't logged in
		//and tried to get it from MySoup
		if (profileFragment.getUserID() == -1){
			profileFragment.setUserID(MySoup.getUserId());
		}
		profileFragment.onLoggedIn();
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrent(int group, int torrent){

	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(USER_ID, profileFragment.getUserID());
		if (profileFragment.getProfile() != null){
			outState.putString(PROFILE, MySon.toJson(profileFragment.getProfile(), Profile.class));
			outState.putString(RECENTS, MySon.toJson(profileFragment.getRecentTorrents(), UserRecents.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (navDrawer == null || !navDrawer.isDrawerOpen()){
			getMenuInflater().inflate(R.menu.profile, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_refresh:
				profileFragment.refresh();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			//If we're not viewing our own profile go to it
			if (profileFragment.getUserID() != MySoup.getUserId()){
				Intent intent = new Intent(this, ProfileActivity.class);
				intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
				startActivity(intent);
			}
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.torrents))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.artists))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.ARTIST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.requests))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.REQUEST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.users))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			startActivity(intent);
		}
	}
}
