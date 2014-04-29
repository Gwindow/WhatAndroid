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
 * Displays a list of the user's bookmarked torrents
 */
public class TorrentBookmarksFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Bookmarks> {
	private TorrentBookmarkAdapter adapter;
	private Bookmarks bookmarks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		TextView noBookmarks = (TextView)view.findViewById(R.id.no_content_notice);
		noBookmarks.setText("No Bookmarks");
		adapter = new TorrentBookmarkAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (bookmarks != null){
			adapter.addAll(bookmarks.getResponse().getTorrents());
		}
		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			Bundle args = new Bundle();
			args.putInt(BookmarksAsyncLoader.BOOKMARK_TYPE, BookmarksAsyncLoader.TORRENTS);
			getLoaderManager().initLoader(0, args, this);
		}
	}

	@Override
	public Loader<Bookmarks> onCreateLoader(int id, Bundle args){
		getActivity().setProgressBarIndeterminate(false);
		getActivity().setProgressBarIndeterminateVisibility(false);
		return new BookmarksAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Bookmarks> loader, Bookmarks data){
		getActivity().setProgressBarIndeterminateVisibility(false);
		bookmarks = data;
		if (bookmarks == null || !bookmarks.getStatus()){
			Toast.makeText(getActivity(), "Could not load torrent bookmarks", Toast.LENGTH_LONG).show();
		}
		else if (adapter != null){
			adapter.clear();
			adapter.addAll(bookmarks.getResponse().getTorrents());
		}
	}

	@Override
	public void onLoaderReset(Loader<Bookmarks> loader){
	}
}
