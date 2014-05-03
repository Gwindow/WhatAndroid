package what.whatandroid.request;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.widget.Toast;
import api.requests.Request;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.artist.ArtistActivity;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.ViewArtistCallbacks;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * View information about a request
 */
public class RequestActivity extends LoggedInActivity
	implements ViewArtistCallbacks, ViewUserCallbacks, ViewTorrentCallbacks, VoteDialog.VoteDialogListener {
	/**
	 * Param to pass the request id to be shown
	 */
	public final static String REQUEST_ID = "what.whatandroid.REQUEST_ID";
	private final static String REQUEST = "what.whatandroid.REQUEST";
	private RequestFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		FragmentManager manager = getSupportFragmentManager();
		int intentId = getIntent().getIntExtra(REQUEST_ID, 1);
		if (savedInstanceState != null){
			fragment = (RequestFragment)manager.findFragmentById(R.id.container);
		}
		else {
			fragment = RequestFragment.newInstance(intentId);
			manager.beginTransaction().add(R.id.container, fragment).commit();
		}
	}

	@Override
	public void onLoggedIn(){
		fragment.onLoggedIn();
	}

	@Override
	public void viewArtist(int id){
		Intent intent = new Intent(this, ArtistActivity.class);
		intent.putExtra(ArtistActivity.ARTIST_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.TORRENT_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrent(int group, int torrent){
	}

	@Override
	public void addBounty(int request, long amt){
		if (request != -1){
			new AddBountyTask().execute(request, amt);
		}
		else {
			Toast.makeText(this, "Invalid request id", Toast.LENGTH_SHORT).show();
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
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))){
			Intent intent = new Intent(this, BookmarksActivity.class);
			startActivity(intent);
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
		else if (selection.equalsIgnoreCase(getString(R.string.barcode_lookup))){
			Intent intent = new Intent(this, BarcodeActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * Add bounty to some request. params should be { requestId, voteAmount }
	 */
	private class AddBountyTask extends AsyncTask<Number, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Number... params){
			int id = params[0].intValue();
			long amt = params[1].longValue();
			return Request.addBounty(id, amt);
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminate(true);
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(Boolean status){
			setProgressBarIndeterminate(false);
			setProgressBarIndeterminateVisibility(false);
			if (status){
				Toast.makeText(RequestActivity.this, "Bounty added", Toast.LENGTH_LONG).show();
				fragment.refresh();
			}
			else {
				Toast.makeText(RequestActivity.this, "Could not add bounty", Toast.LENGTH_LONG).show();
			}
		}
	}
}
