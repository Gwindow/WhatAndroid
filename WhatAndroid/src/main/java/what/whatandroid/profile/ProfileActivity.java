package what.whatandroid.profile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Window;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.index.Index;
import api.search.user.UserSearch;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.inbox.InboxActivity;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.search.UserSearchAsyncLoader;
import what.whatandroid.subscriptions.SubscriptionsActivity;
import what.whatandroid.top10.Top10Activity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

public class ProfileActivity extends LoggedInActivity implements ViewTorrentCallbacks,
	LoaderManager.LoaderCallbacks<UserSearch>, LoadingListener<Index> {
	/**
	 * Param to pass the user id to display to the activity
	 */
	public static final String USER_ID = "what.whatandroid.USER_ID";
	private static final Pattern userIdPattern = Pattern.compile(".*id=(\\d+).*"),
		userSearchPattern = Pattern.compile(".*search=([^&]+).*");
	private ProfileFragment profileFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		Intent intent = getIntent();
		int id = intent.getIntExtra(USER_ID, MySoup.getUserId());
		//If we're loading from a saved state then recover the fragment
		if (savedInstanceState != null){
			profileFragment = (ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		}
		else {
			String terms = null;
			if (intent.getScheme() != null && intent.getDataString() != null && intent.getDataString().contains("what.cd")){
				Matcher m = userIdPattern.matcher(intent.getDataString());
				if (m.find()){
					id = Integer.parseInt(m.group(1));
				}
				//If no user id we could be receiving a search intent
				else {
					m = userSearchPattern.matcher(intent.getDataString());
					if (m.find()){
						Bundle args = new Bundle();
						try {
							terms = URLDecoder.decode(m.group(1), "UTF-8");
						}
						catch (UnsupportedEncodingException e){
							e.printStackTrace();
						}
						args.putString(SearchActivity.TERMS, terms);
						getSupportLoaderManager().initLoader(0, args, this);
					}
				}
			}
			profileFragment = ProfileFragment.newInstance(id, terms != null);
			getSupportFragmentManager().beginTransaction().add(R.id.container, profileFragment).commit();
		}
	}

	@Override
	public void onLoggedIn(){
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
	public Loader<UserSearch> onCreateLoader(int id, Bundle args){
		setProgressBarIndeterminate(true);
		setProgressBarIndeterminateVisibility(true);
		return new UserSearchAsyncLoader(this, args);
	}

	@Override
	public void onLoadFinished(Loader<UserSearch> loader, UserSearch data){
		setProgressBarIndeterminateVisibility(false);
		if (data == null || !data.getStatus()){
			Toast.makeText(this, "Could not load user", Toast.LENGTH_LONG).show();
		}
		else if (data.getResponse().getResults().size() == 1){
			profileFragment.setUserID(data.getResponse().getResults().get(0).getUserId().intValue());
		}
		else {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			Matcher m = userSearchPattern.matcher(intent.getDataString());
			String terms = "";
			if (m.find()){
				try {
					terms = URLDecoder.decode(m.group(1), "UTF-8");
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}
			intent.putExtra(SearchActivity.TERMS, terms);
			startActivity(intent);
		}
	}

	@Override
	public void onLoadingComplete(Index data){
		navDrawer.updateNotifications(PreferenceManager.getDefaultSharedPreferences(this));
	}

	@Override
	public void onLoaderReset(Loader<UserSearch> loader){
	}

	@Override
	public void onBackPressed(){
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0){
			fm.popBackStackImmediate();
			profileFragment = (ProfileFragment)fm.findFragmentById(R.id.container);
			profileFragment.onLoggedIn();
		}
		else {
			super.onBackPressed();
		}
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
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			//If we're not viewing our own profile go to it
			if (profileFragment.getUserID() != MySoup.getUserId()){
				profileFragment = ProfileFragment.newInstance(MySoup.getUserId(), false);
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, profileFragment)
					.addToBackStack(null)
					.commit();
				profileFragment.onLoggedIn();
			}
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
		else if (selection.equalsIgnoreCase(getString(R.string.forums))){
			Intent intent = new Intent(this, ForumActivity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.top10))){
			Intent intent = new Intent(this, Top10Activity.class);
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
