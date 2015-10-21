package what.whatandroid.forums;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.forum.thread.Poll;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.AddQuoteCallback;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewForumCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.forums.categories.ForumCategoriesFragment;
import what.whatandroid.forums.forum.ForumFragment;
import what.whatandroid.forums.poll.PollDialog;
import what.whatandroid.forums.thread.ThreadFragment;
import what.whatandroid.inbox.InboxActivity;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.subscriptions.SubscriptionsActivity;
import what.whatandroid.top10.Top10Activity;

/**
 * Activity for viewing the forums
 */
public class ForumActivity extends LoggedInActivity implements ViewUserCallbacks,
	ViewForumCallbacks, PollDialog.PollDialogListener, AddQuoteCallback {
	public static final String FORUM_ID = "what.whatandroid.forums.FORUM_ID",
		THREAD_ID = "what.whatandroid.forums.THREAD_ID",
		PAGE = "what.whatandroid.forums.PAGE",
			POST_ID = "what.whatandroid.forums.POST_ID",
			CATEGORY_TAG = "what.whatandroid.forums.categoriesfragment";
	/**
	 * Matchers to match against forum url links
	 */
	private static final Pattern forumId = Pattern.compile(".*forumid=(\\d+).*"),
		threadId = Pattern.compile(".*threadid=(\\d+).*"),
		page = Pattern.compile(".*page=(\\d+).*"),
		postId = Pattern.compile(".*postid=(\\d+).*");

	/**
	 * Logged in callback to the fragment being shown so we can let it know
	 * when to start loading
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
			loginListener = (OnLoggedInCallback)fm.findFragmentById(R.id.container);
		}
		else {
			//Determine what part of the forums we want to view, eg. jump to a post, thread or forum
			Fragment f;
			Intent intent = getIntent();
			String tag = null;
			//If we're coming from some link to the forums parse it and return the corresponding fragment
			if (intent.getScheme() != null && intent.getDataString() != null && intent.getDataString().contains("what.cd")){
				f = parseLink(intent.getDataString());
			}
			//Jumping to a post in some thread
			else if (intent.hasExtra(THREAD_ID) && intent.hasExtra(POST_ID)){
				f = ThreadFragment.newInstance(intent.getIntExtra(THREAD_ID, 0), intent.getIntExtra(POST_ID, 0));
			}
			//Jumping to a thread
			else if (intent.hasExtra(THREAD_ID)){
				f = ThreadFragment.newInstance(intent.getIntExtra(THREAD_ID, 0));
			}
			//Jumping to a forum
			else if (intent.hasExtra(FORUM_ID)){
				f = ForumFragment.newInstance(intent.getIntExtra(FORUM_ID, 0));
			}
			//Not jumping anywhere, just going to the regular categories view
			else {
				f = new ForumCategoriesFragment();
				tag = CATEGORY_TAG;
			}
			loginListener = (OnLoggedInCallback)f;
			fm.beginTransaction().add(R.id.container, f, tag).commit();
		}
	}

	/**
	 * Parse the forum link url for the appropriate forum fragment to display and return it
	 * Will return the categories view if we can't parse where to go or don't support
	 * it (eg. forum search)
	 */
	private Fragment parseLink(String url){
		//Handle forum links
		Matcher m = forumId.matcher(url);
		if (m.find()){
			return ForumFragment.newInstance(Integer.parseInt(m.group(1)));
		}
		//Handle thread links
		m = threadId.matcher(url);
		if (m.find()){
			int thread = Integer.parseInt(m.group(1));
			m = postId.matcher(url);
			//If we're also linking to a post within the thread
			if (m.find()){
				return ThreadFragment.newInstance(thread, Integer.parseInt(m.group(1)));
			}
			return ThreadFragment.newInstance(thread);
		}
		return new ForumCategoriesFragment();
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
				.addToBackStack(CATEGORY_TAG)
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
		new PollVoteTask().execute(thread, vote);
		//Also notify the thread fragment showing that it should update the poll it's caching
		ThreadFragment fragment = (ThreadFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		if (fragment != null){
			fragment.updatePoll(vote);
		}
	}

	@Override
	public void quote(String quote){
		//Notify the thread fragment about the quote so it can be added to the draft
		ThreadFragment fragment = (ThreadFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		if (fragment != null){
			fragment.quote(quote);
		}
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
		else if (selection.contains(getString(R.string.messages))){
			Intent intent = new Intent(this, InboxActivity.class);
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
		else if (selection.equalsIgnoreCase(getString(R.string.top10))){
			Intent intent = new Intent(this, Top10Activity.class);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.forums))) {
			//Need to check if the category fragment is in the back stack since we may have come
			//here through an intent and not through the categories view
			FragmentManager fm = getSupportFragmentManager();
			if (fm.findFragmentByTag(CATEGORY_TAG) != null) {
				fm.popBackStack(CATEGORY_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				ForumCategoriesFragment f = new ForumCategoriesFragment();
				loginListener = f;
				fm.beginTransaction().replace(R.id.container, f, CATEGORY_TAG).commit();
				if (MySoup.isLoggedIn()) {
					loginListener.onLoggedIn();
				}
			}
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

	/**
	 * Makes a vote on a poll in the background, params should be { thread, vote }
	 */
	private class PollVoteTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params){
			return Poll.vote(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (!status){
				Toast.makeText(ForumActivity.this, "Could not vote on poll", Toast.LENGTH_LONG).show();
			}
		}
	}
}
