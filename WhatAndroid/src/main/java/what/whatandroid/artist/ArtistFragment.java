package what.whatandroid.artist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import api.soup.MySoup;
import api.torrents.artist.Artist;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment for viewing an artist's information and torrent groups
 */
public class ArtistFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Artist> {
	/**
	 * The artist being viewed
	 */
	private Artist artist;
	/**
	 * Adapter containing the fragments displaying the artist information
	 */
	private ArtistPagerAdapter pagerAdapter;
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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		pagerAdapter = new ArtistPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		tabs.setViewPager(viewPager);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
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
				updateMenus();
				pagerAdapter.onLoadingComplete(artist);
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
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_24dp);
			}
			else {
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_border_24dp);
			}
			if (artist.getResponse().hasNotificationsEnabled()){
				notificationMenu.setIcon(R.drawable.ic_visibility_24dp);
			}
			else {
				notificationMenu.setIcon(R.drawable.ic_visibility_off_24dp);
			}
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
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_border_24dp);
			}
			else {
				bookmarkMenu.setIcon(R.drawable.ic_bookmark_24dp);
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
				notificationMenu.setIcon(R.drawable.ic_visibility_off_24dp);
			}
			else {
				notificationMenu.setIcon(R.drawable.ic_visibility_24dp);
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
