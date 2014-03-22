package what.whatandroid.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.ArtistSearchFragment;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * View information about the artist and a list of their torrent groups
 */
public class ArtistActivity extends LoggedInActivity implements ViewTorrentCallbacks {
	/**
	 * Param to pass the user id to display to the activity
	 * the USE_SEARCH parameter should be set to true and will indicate that the artist
	 * to be viewed is coming from the ArtistSearchFragment
	 */
	public final static String ARTIST_ID = "what.whatandroid.ARTIST_ID",
		USE_SEARCH = "what.whatandroid.USE_SEARCH";
	private ArtistFragment artistFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		int id = getIntent().getIntExtra(ARTIST_ID, 1);
		boolean useSearch = getIntent().getBooleanExtra(USE_SEARCH, false);
		//If we're coming from the ArtistSearchFragment then the artist was already loaded over there, so re-use it
		if (useSearch){
			if (ArtistSearchFragment.getArtist() != null){
				artistFragment = ArtistFragment.newInstance(ArtistSearchFragment.getArtist());
			}
		}
		//If no artist from search then download the data
		if (artistFragment == null){
			artistFragment = ArtistFragment.newInstance(id);
		}
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, artistFragment).commit();
	}

	@Override
	public void onLoggedIn(){
		System.out.println("ArtistActivity onLoggedIn");
		artistFragment.onLoggedIn();
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
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
		else if (selection.equalsIgnoreCase(getString(R.string.users))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			startActivity(intent);
		}
	}
}
