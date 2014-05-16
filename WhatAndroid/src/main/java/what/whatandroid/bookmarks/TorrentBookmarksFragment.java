package what.whatandroid.bookmarks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.bookmarks.Bookmarks;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Displays a list of the user's bookmarked torrents
 */
public class TorrentBookmarksFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Bookmarks>,
	BookmarksChangedListener {
	public static final String BOOKMARKS_CHANGED = "what.whatandroid.BOOKMARKS_CHANGED";
	private boolean reloadBookmarks;
	private TorrentBookmarkAdapter adapter;
	private Bookmarks bookmarks;
	private ProgressBar loadingIndicator;
	private TextView noBookmarks;

	public TorrentBookmarksFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null){
			reloadBookmarks = savedInstanceState.getBoolean(BOOKMARKS_CHANGED, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		noBookmarks = (TextView)view.findViewById(R.id.no_content_notice);
		noBookmarks.setText("No Bookmarks");
		adapter = new TorrentBookmarkAdapter(getActivity(), this, noBookmarks);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (bookmarks != null){
			if (bookmarks.getResponse().getTorrents().isEmpty()){
				noBookmarks.setVisibility(View.VISIBLE);
			}
			else {
				adapter.addAll(bookmarks.getResponse().getTorrents());
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
		outState.putBoolean(BOOKMARKS_CHANGED, reloadBookmarks);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			Bundle args = new Bundle();
			args.putInt(BookmarksAsyncLoader.BOOKMARK_TYPE, BookmarksAsyncLoader.TORRENTS);
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
		loadingIndicator.setVisibility(View.VISIBLE);
		return new BookmarksAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Bookmarks> loader, Bookmarks data){
		loadingIndicator.setVisibility(View.GONE);
		bookmarks = data;
		if (bookmarks == null || !bookmarks.getStatus()){
			Toast.makeText(getActivity(), "Could not load torrent bookmarks", Toast.LENGTH_LONG).show();
		}
		else if (adapter != null){
			adapter.clear();
			if (bookmarks.getResponse().getTorrents().isEmpty()){
				noBookmarks.setVisibility(View.VISIBLE);
			}
			else {
				noBookmarks.setVisibility(View.GONE);
				adapter.addAll(bookmarks.getResponse().getTorrents());
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
