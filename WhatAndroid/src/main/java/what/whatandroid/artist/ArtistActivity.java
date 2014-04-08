package what.whatandroid.artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.request.RequestActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * View information about the artist and a list of their torrent groups
 */
public class ArtistActivity extends LoggedInActivity implements ViewTorrentCallbacks, ViewRequestCallbacks {
	/**
	 * Param to pass the user id to display to the activity
	 * the USE_SEARCH parameter should be set to true and will indicate that the artist
	 * to be viewed is coming from the ArtistSearchFragment
	 */
	public static final String ARTIST_ID = "what.whatandroid.ARTIST_ID", ARTIST_NAME = "what.whatandroid.ARTIST_NAME",
		USE_SEARCH = "what.whatandroid.USE_SEARCH";
	private ArtistFragment artistFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		int id = getIntent().getIntExtra(ARTIST_ID, 1);
		boolean useSearch = getIntent().getBooleanExtra(USE_SEARCH, false);
		if (savedInstanceState != null){
			artistFragment = (ArtistFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		}
		else {
			artistFragment = ArtistFragment.newInstance(id, useSearch);
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
