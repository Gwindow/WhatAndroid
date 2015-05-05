package what.whatandroid.request;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.inbox.InboxActivity;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.notifications.NotificationsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.subscriptions.SubscriptionsActivity;
import what.whatandroid.top10.Top10Activity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * View information about a request
 */
public class RequestActivity extends LoggedInActivity
	implements ViewArtistCallbacks, ViewUserCallbacks, ViewTorrentCallbacks, VoteDialog.VoteDialogListener {

	public static final String ADDBOUNTY_FILTER = "RequestActivity_receiver";
	/**
	 * Param to pass the request id to be shown
	 */
	public final static String REQUEST_ID = "what.whatandroid.REQUEST_ID";
	private RequestFragment fragment;
	private AddBountyReceiver receiver;

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
		receiver = new AddBountyReceiver();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ADDBOUNTY_FILTER));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrent(int group, int torrent){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, group);
		intent.putExtra(TorrentGroupActivity.TORRENT_ID, torrent);
		startActivity(intent);
	}

	@Override
	public void addBounty(int request, long amt){
		if (request != -1){
			Intent addBounty = new Intent(this, AddBountyService.class);
			receiver.setProgressBar();
			Number[] params = { request, amt };
			addBounty.putExtra("params", params);
			this.startService(addBounty);
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

	private class AddBountyReceiver extends BroadcastReceiver {
		public void setProgressBar(){
			setProgressBarIndeterminate(true);
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onReceive(Context receiverContext, Intent receiverIntent){
			boolean status = receiverIntent.getBooleanExtra("status", false);
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

	/**
	 * Add bounty to some request. params should be { requestId, voteAmount }
	 */
	public static class AddBountyService extends IntentService {
		public AddBountyService(){
			super("AddBountyService");
		}

		public void onHandleIntent(Intent intent){
			Number[] params = (Number[]) intent.getSerializableExtra("params");
			int id = params[0].intValue();
			long amt = params[1].longValue();
			Intent resultIntent = new Intent(ADDBOUNTY_FILTER);
			resultIntent.putExtra("status", Request.addBounty(id, amt));
			LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
			return;
		}
	}
}
