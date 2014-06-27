package what.whatandroid.forums;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewForumCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.forums.categories.ForumCategoriesFragment;
import what.whatandroid.forums.forum.ForumFragment;
import what.whatandroid.forums.poll.PollDialog;
import what.whatandroid.forums.thread.ThreadFragment;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;

/**
 * Activity for viewing the forums
 */
public class ForumActivity extends LoggedInActivity implements ViewUserCallbacks, ViewForumCallbacks, PollDialog.PollDialogListener {
	public static final String FORUM_ID = "what.whatandroid.forums.FORUM_ID",
		THREAD_ID = "what.whatandroid.forums.THREAD_ID",
		PAGE = "what.whatandroid.forums.PAGE",
		POST_ID = "what.whatandroid.forums.POST_ID";

	/**
	 * Logged in callback to the fragment being shown so we can let it know
	 * when to start loading
	 */
	private OnLoggedInCallback loginListener;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null){
			Fragment f = fm.findFragmentById(R.id.container);
			loginListener = (OnLoggedInCallback)f;
		}
		else {
			//Later parse intent and show what we want
			Fragment f = new ForumCategoriesFragment();
			loginListener = (OnLoggedInCallback)f;
			fm.beginTransaction().add(R.id.container, f).commit();
		}
	}

	@Override
	public void onBackPressed(){
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0){
			fm.popBackStackImmediate();
			loginListener = (OnLoggedInCallback)fm.findFragmentById(R.id.container);
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public void onLoggedIn(){
		loginListener.onLoggedIn();
	}

	@Override
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewForum(int id){
		ForumFragment f = ForumFragment.newInstance(id);
		loginListener = f;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, f)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void viewThread(int id){
		ThreadFragment f = ThreadFragment.newInstance(id);
		loginListener = f;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, f)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void viewThread(int id, int postId){
		ThreadFragment f = ThreadFragment.newInstance(id, postId);
		loginListener = f;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, f)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void makeVote(int thread, int vote){
		System.out.println("Making vote on thread " + thread + " on answer " + vote);
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
		else if (selection.equalsIgnoreCase(getString(R.string.notifications)) || selection.equalsIgnoreCase(getString(R.string.new_notifications))){
			Intent intent = new Intent(this, NotificationsActivity.class);
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
