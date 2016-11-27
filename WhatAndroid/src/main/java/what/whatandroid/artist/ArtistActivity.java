package what.whatandroid.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.ViewArtistCallbacks;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.inbox.InboxActivity;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.request.RequestActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.subscriptions.SubscriptionsActivity;
import what.whatandroid.top10.Top10Activity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * View information about the artist and a list of their torrent groups
 */
public class ArtistActivity extends LoggedInActivity implements ViewTorrentCallbacks, ViewRequestCallbacks, ViewArtistCallbacks {
	/**
	 * Param to pass the user id to display to the activity
	 * the USE_SEARCH parameter should be set to true and will indicate that the artist
	 * to be viewed is coming from the ArtistSearchFragment
	 */
	public static final String ARTIST_ID = "what.whatandroid.ARTIST_ID", ARTIST_NAME = "what.whatandroid.ARTIST_NAME",
		USE_SEARCH = "what.whatandroid.USE_SEARCH";
	/**
	 * Patterns to match artist names and ids
	 */
	private final static Pattern artistName = Pattern.compile(".*artistname=([^&]+).*"),
		artistId = Pattern.compile(".*id=(\\d+).*");
	private ArtistFragment artistFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		if (savedInstanceState != null){
			artistFragment = (ArtistFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		}
		else {
			Intent intent = getIntent();
			//Use -1 to force a load failure if no artist id or name was passed
			int id = intent.getIntExtra(ARTIST_ID, -1);
			String name = intent.getStringExtra(ARTIST_NAME);
			boolean useSearch = intent.getBooleanExtra(USE_SEARCH, false);
			//If we're opening an artist url parse out the artist we're trying to view
			String site = MySoup.getSite();
			if (intent.getScheme() != null && intent.getData() != null && intent.getData().toString().contains(site)){
				String uri = intent.getData().toString();
				Matcher m = artistName.matcher(uri);
				if (m.find()){
					name = m.group(1);
					try {
						//Remove the url formatting in the name
						name = URLDecoder.decode(name, "UTF-8");
					}
					catch (UnsupportedEncodingException e){
						e.printStackTrace();
					}
				}
				else {
					//Is it possible that people will send the artist id instead of the name?
					m = artistId.matcher(uri);
					if (m.find()){
						id = Integer.parseInt(m.group(1));
					}
				}
			}
			artistFragment = ArtistFragment.newInstance(id, name, useSearch);
			getSupportFragmentManager().beginTransaction().add(R.id.container, artistFragment).commit();
		}
	}

	@Override
	public void onLoggedIn(){
		artistFragment.onLoggedIn();
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
	public void viewRequest(int id){
		Intent intent = new Intent(this, RequestActivity.class);
		intent.putExtra(RequestActivity.REQUEST_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewArtist(int id){
		artistFragment = ArtistFragment.newInstance(id, null, false);
		setTitle(getString(R.string.artist));
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, artistFragment)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void onBackPressed(){
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0){
			setTitle(getString(R.string.artist));
			fm.popBackStackImmediate();
			artistFragment = (ArtistFragment)fm.findFragmentById(R.id.container);
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
