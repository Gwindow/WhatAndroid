package what.whatandroid.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.request.RequestActivity;
import what.whatandroid.subscriptions.SubscriptionsActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * Activity for performing Torrent, User or Request searches. The searching itself is handled
 * by the specific fragments
 */
public class SearchActivity extends LoggedInActivity
	implements ViewTorrentCallbacks, ViewUserCallbacks, ViewRequestCallbacks, OnLoggedInCallback {
	/**
	 * Param to pass the search type desired and terms and tags if desired
	 */
	public static final String SEARCH = "what.whatandroid.SEARCH", TERMS = "what.whatandroid.SEARCH.TERMS",
		TAGS = "what.whatandroid.SEARCH.TAGS", PAGE = "what.whatandroid.SEARCH_PAGE";
	/**
	 * The parameters to specify what we want to search for
	 */
	public static final int TORRENT = 0, ARTIST = 1, USER = 2, REQUEST = 3;
	private int type;
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

		Intent intent = getIntent();
		if (savedInstanceState != null){
			type = savedInstanceState.getInt(SEARCH);
		}
		else {
			type = intent.getIntExtra(SEARCH, TORRENT);
		}
		String terms = intent.getStringExtra(TERMS);
		String tags = intent.getStringExtra(TAGS);

		Fragment fragment;
		if (savedInstanceState == null){
			switch (type){
				case ARTIST:
					fragment = ArtistSearchFragment.newInstance(terms);
					break;
				case USER:
					fragment = UserSearchFragment.newInstance(terms);
					break;
				case REQUEST:
					fragment = RequestSearchFragment.newInstance(terms, tags);
					break;
				default:
					fragment = TorrentSearchFragment.newInstance(terms, tags);
					break;
			}
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment).commit();
		}
		else {
			fragment = getSupportFragmentManager().findFragmentById(R.id.container);
		}
		searchFragment = (OnLoggedInCallback)fragment;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//Eventually also save the terms, tags & maybe search results?
		//maybe only first page of results to speed things up
		outState.putInt(SEARCH, type);
	}

	@Override
	public void onLoggedIn(){
		searchFragment.onLoggedIn();
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
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewRequest(int id){
		Intent intent = new Intent(this, RequestActivity.class);
		intent.putExtra(RequestActivity.REQUEST_ID, id);
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
		else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))){
			Intent intent = new Intent(this, BookmarksActivity.class);
			startActivity(intent);
		}
		else if (selection.contains(getString(R.string.notifications))){
			Intent intent = new Intent(this, NotificationsActivity.class);
			startActivity(intent);
		}
		else if (selection.contains(getString(R.string.subscriptions))){
			Intent intent = new Intent(this, SubscriptionsActivity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.torrents)) && type != TORRENT){
			FragmentManager fm = getSupportFragmentManager();
			TorrentSearchFragment f = TorrentSearchFragment.newInstance("", "");
			searchFragment = f;
			fm.beginTransaction().replace(R.id.container, f).commit();
			type = TORRENT;
		}
		else if (selection.equalsIgnoreCase(getString(R.string.artists)) && type != ARTIST){
			FragmentManager fm = getSupportFragmentManager();
			ArtistSearchFragment f = ArtistSearchFragment.newInstance("");
			searchFragment = f;
			fm.beginTransaction().replace(R.id.container, f).commit();
			type = ARTIST;
		}
		else if (selection.equalsIgnoreCase(getString(R.string.requests)) && type != REQUEST){
			FragmentManager fm = getSupportFragmentManager();
			RequestSearchFragment f = RequestSearchFragment.newInstance("", "");
			searchFragment = f;
			fm.beginTransaction().replace(R.id.container, f).commit();
			type = REQUEST;
		}
		else if (selection.equalsIgnoreCase(getString(R.string.users)) && type != USER){
			FragmentManager fm = getSupportFragmentManager();
			UserSearchFragment f = UserSearchFragment.newInstance("");
			searchFragment = f;
			fm.beginTransaction().replace(R.id.container, f).commit();
			type = USER;
		}
		else if (selection.equalsIgnoreCase(getString(R.string.barcode_lookup))){
			Intent intent = new Intent(this, BarcodeActivity.class);
			startActivity(intent);
		}
	}
}
