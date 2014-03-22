package what.whatandroid.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * Activity for performing Torrent, User or Request searches. The searching itself is handled
 * by the specific fragments
 */
public class SearchActivity extends LoggedInActivity implements ViewTorrentCallbacks, ViewUserCallbacks, OnLoggedInCallback {
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
	 * The OnLoggedInCallback for the search fragment, so we can tell it that it's ok to start loading
	 * an existing search if it has one
	 */
	private OnLoggedInCallback searchFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		String type = getIntent().getStringExtra(SEARCH);
		String terms = getIntent().getStringExtra(TERMS);
		String tags = getIntent().getStringExtra(TAGS);

		Fragment fragment;
		if (type == null){
			fragment = TorrentSearchFragment.newInstance(terms, tags);
		}
		else if (type.equalsIgnoreCase(ARTIST)){
			fragment = ArtistSearchFragment.newInstance(terms);
		}
		else if (type.equalsIgnoreCase(USER)){
			fragment = UserSearchFragment.newInstance(terms);
		}
		/*
		if (type.equalsIgnoreCase(REQUEST)){
			setTitle("Request Search");
			//set new request search fragment
		}
		*/
		else {
			fragment = TorrentSearchFragment.newInstance(terms, tags);
		}
		searchFragment = (OnLoggedInCallback)fragment;
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, fragment).commit();
	}

	@Override
	public void onLoggedIn(){
		System.out.println("SearchActivity onLoggedIn");
		searchFragment.onLoggedIn();
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
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
		else if (selection.equalsIgnoreCase(getString(R.string.torrents))){
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction().replace(R.id.container, new TorrentSearchFragment()).commit();
		}
		else if (selection.equalsIgnoreCase(getString(R.string.artists))){
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction().replace(R.id.container, new ArtistSearchFragment()).commit();
		}
		else if (selection.equalsIgnoreCase(getString(R.string.users))){
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction().replace(R.id.container, new UserSearchFragment()).commit();
		}
	}
}
