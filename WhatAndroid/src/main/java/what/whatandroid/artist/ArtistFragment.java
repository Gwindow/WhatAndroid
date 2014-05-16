package what.whatandroid.artist;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import api.torrents.artist.Artist;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.views.ImageDialog;

/**
 * Fragment for viewing an artist's information and torrent groups
 */
public class ArtistFragment extends Fragment implements OnLoggedInCallback, View.OnClickListener,
	LoaderManager.LoaderCallbacks<Artist> {
	/**
	 * The artist being viewed
	 */
	private Artist artist;
	/**
	 * Callbacks to the activity so we can set the title
	 */
	private SetTitleCallback callbacks;
	/**
	 * Various content views displaying the artist information
	 */
	private ImageView image;
	private ProgressBar spinner;
	private View artContainer;
	private ExpandableListView torrentList;
	/**
	 * Menu items for toggling bookmarks/notifications status
	 */
	private MenuItem bookmarkMenu, notificationMenu;

	/**
	 * Use this factory method to create a new artist fragment displaying information about
	 * the artist with the id
	 *
	 * @param id        artist id to load
	 * @param name      artist name to load
	 * @param useSearch true if the artist information was loaded by the ArtistSearch fragment
	 *                  and we should get it from there
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(int id, String name, boolean useSearch){
		ArtistFragment f = new ArtistFragment();
		Bundle args = new Bundle();
		args.putInt(ArtistActivity.ARTIST_ID, id);
		args.putString(ArtistActivity.ARTIST_NAME, name);
		args.putBoolean(ArtistActivity.USE_SEARCH, useSearch);
		f.setArguments(args);
		return f;
	}

	public ArtistFragment(){
		//Required empty public ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewTorrentCallbacks!");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);
		View header = inflater.inflate(R.layout.header_image, null);
		torrentList.addHeaderView(header);
		image = (ImageView)header.findViewById(R.id.image);
		image.setOnClickListener(this);
		spinner = (ProgressBar)header.findViewById(R.id.loading_indicator);
		artContainer = header.findViewById(R.id.art_container);
		if (artist != null){
			populateViews();
		}
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.artist, menu);
		bookmarkMenu = menu.findItem(R.id.action_bookmark);
		notificationMenu = menu.findItem(R.id.action_notifications);
		if (artist != null){
			updateMenus();
		}
		else {
			bookmarkMenu.setVisible(false);
			notificationMenu.setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_bookmark:
				new ToggleBookmarkTask().execute();
				return true;
			case R.id.action_notifications:
				new ToggleNotificationsTask().execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(artist.getResponse().getImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
	}

	@Override
	public Loader<Artist> onCreateLoader(int id, Bundle args){
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		return new ArtistAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Artist> loader, Artist data){
		artist = data;
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(false);
			getActivity().setProgressBarIndeterminateVisibility(false);
			if (artist != null && artist.getStatus()){
				populateViews();
				updateMenus();
			}
			else {
				Toast.makeText(getActivity(), "Could not load artist", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Artist> loader){
	}

	/**
	 * Update the bookmark and notification menu status icons
	 */
	private void updateMenus(){
		if (bookmarkMenu != null && notificationMenu != null){
			bookmarkMenu.setVisible(true);
			notificationMenu.setVisible(true);
			if (artist.getResponse().isBookmarked()){
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_on);
			}
			else {
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_off);
			}
			if (artist.getResponse().hasNotificationsEnabled()){
				notificationMenu.setIcon(R.drawable.ic_eye_on);
			}
			else {
				notificationMenu.setIcon(R.drawable.ic_eye_off);
			}
		}
	}

	/**
	 * Update all the artist information with the loaded api request
	 */
	private void populateViews(){
		callbacks.setTitle(artist.getResponse().getName());
		String imgUrl = artist.getResponse().getImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner, artContainer));
		}
		else {
			artContainer.setVisibility(View.GONE);
		}
		if (torrentList.getAdapter() == null){
			ArtistTorrentAdapter adapter = new ArtistTorrentAdapter(getActivity(), artist.getReleases().flatten(),
				artist.getResponse().getRequests());
			torrentList.setAdapter(adapter);
			torrentList.setOnChildClickListener(adapter);
		}
	}

	/**
	 * Async task to toggle the artists bookmark status
	 */
	private class ToggleBookmarkTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params){
			if (artist.getResponse().isBookmarked()){
				return artist.removeBookmark();
			}
			return artist.addBookmark();
		}

		@Override
		protected void onPreExecute(){
			//Display action as successful as we load
			if (artist.getResponse().isBookmarked()){
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_off);
			}
			else {
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_on);
			}
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(true);
				getActivity().setProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(false);
				getActivity().setProgressBarIndeterminateVisibility(false);
			}
			if (!status){
				if (artist.getResponse().isBookmarked()){
					Toast.makeText(getActivity(), "Could not remove bookmark", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getActivity(), "Could not add bookmark", Toast.LENGTH_LONG).show();
				}
			}
			updateMenus();
		}
	}

	/**
	 * Async task to toggle the artist's notification status
	 */
	private class ToggleNotificationsTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params){
			if (artist.getResponse().hasNotificationsEnabled()){
				return artist.disableNotifications();
			}
			return artist.enableNotifications();
		}

		@Override
		protected void onPreExecute(){
			//Display action as successful while we load
			if (artist.getResponse().hasNotificationsEnabled()){
				notificationMenu.setIcon(R.drawable.ic_eye_off);
			}
			else {
				notificationMenu.setIcon(R.drawable.ic_eye_on);
			}
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(true);
				getActivity().setProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(false);
				getActivity().setProgressBarIndeterminateVisibility(false);
			}
			if (!status){
				if (artist.getResponse().hasNotificationsEnabled()){
					Toast.makeText(getActivity(), "Could not remove notifications", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getActivity(), "Could not enable notifications", Toast.LENGTH_LONG).show();
				}
			}
			updateMenus();
		}
	}
}
