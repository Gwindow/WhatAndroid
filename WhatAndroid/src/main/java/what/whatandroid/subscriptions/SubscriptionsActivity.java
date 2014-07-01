package what.whatandroid.subscriptions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;

import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewForumCallbacks;
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;

/**
 * Subscriptions activity lets the user view and interact with
 * the forum threads they're subscribed to
 */
public class SubscriptionsActivity extends LoggedInActivity implements ViewForumCallbacks {
	/**
	 * Logged in callback to the fragment being shown so we can let
	 * it know when to start loading
	 */
	private OnLoggedInCallback loginListener;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null){
			loginListener = (OnLoggedInCallback) fm.findFragmentById(R.id.container);
		}
		else {
			SubscriptionsFragment f = new SubscriptionsFragment();
			loginListener = f;
			fm.beginTransaction().add(R.id.container, f).commit();
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.edit()
			.putBoolean(getString(R.string.key_pref_new_subscriptions), false)
			.apply();
	}

	@Override
	public void onLoggedIn(){
		loginListener.onLoggedIn();
	}

	@Override
	public void viewForum(int id){
		Intent intent = new Intent(this, ForumActivity.class);
		intent.putExtra(ForumActivity.FORUM_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewThread(int id){
		Intent intent = new Intent(this, ForumActivity.class);
		intent.putExtra(ForumActivity.THREAD_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewThread(int id, int postId){
		Intent intent = new Intent(this, ForumActivity.class);
		intent.putExtra(ForumActivity.THREAD_ID, id);
		intent.putExtra(ForumActivity.POST_ID, postId);
		startActivity(intent);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		//Pass an argument to the activity telling it which to show?
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
		else if (selection.equalsIgnoreCase(getString(R.string.forums))){
			Intent intent = new Intent(this, ForumActivity.class);
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
		else if (selection.equalsIgnoreCase(getString(R.string.barcode_lookup))){
			Intent intent = new Intent(this, BarcodeActivity.class);
			startActivity(intent);
		}
	}
}
