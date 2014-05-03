package what.whatandroid.bookmarks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.bookmarks.Bookmarks;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Displays a list of the user's bookmarked artists
 */
public class ArtistBookmarksFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Bookmarks>,
	BookmarksChangedListener {
	private boolean reloadBookmarks;
	private ArtistBookmarkAdapter adapter;
	private Bookmarks bookmarks;
	private TextView noBookmarks;

	public ArtistBookmarksFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null){
			reloadBookmarks = savedInstanceState.getBoolean(TorrentBookmarksFragment.BOOKMARKS_CHANGED, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		noBookmarks = (TextView)view.findViewById(R.id.no_content_notice);
		noBookmarks.setText("No Bookmarks");
		adapter = new ArtistBookmarkAdapter(getActivity(), this, noBookmarks);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (bookmarks != null){
			if (bookmarks.getResponse().getArtists().isEmpty()){
				noBookmarks.setVisibility(View.VISIBLE);
			}
			else {
				adapter.addAll(bookmarks.getResponse().getArtists());
			}
		}
		else if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean(TorrentBookmarksFragment.BOOKMARKS_CHANGED, reloadBookmarks);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			Bundle args = new Bundle();
			args.putInt(BookmarksAsyncLoader.BOOKMARK_TYPE, BookmarksAsyncLoader.ARTISTS);
			if (!reloadBookmarks){
				getLoaderManager().initLoader(0, args, this);
			}
			else {
				reloadBookmarks = false;
				getLoaderManager().restartLoader(0, args, this);
			}
		}
	}

	@Override
	public Loader<Bookmarks> onCreateLoader(int id, Bundle args){
		getActivity().setProgressBarIndeterminate(true);
		getActivity().setProgressBarIndeterminateVisibility(true);
		return new BookmarksAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Bookmarks> loader, Bookmarks data){
		getActivity().setProgressBarIndeterminateVisibility(false);
		bookmarks = data;
		if (bookmarks == null || !bookmarks.getStatus()){
			Toast.makeText(getActivity(), "Could not load artist bookmarks", Toast.LENGTH_LONG).show();
		}
		else if (adapter != null){
			adapter.clear();
			if (bookmarks.getResponse().getArtists().isEmpty()){
				noBookmarks.setVisibility(View.VISIBLE);
			}
			else {
				noBookmarks.setVisibility(View.GONE);
				adapter.addAll(bookmarks.getResponse().getArtists());
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Bookmarks> loader){
	}

	@Override
	public void bookmarksChanged(){
		reloadBookmarks = true;
	}
}
