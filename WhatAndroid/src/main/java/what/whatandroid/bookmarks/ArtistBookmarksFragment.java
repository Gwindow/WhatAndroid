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
import api.bookmarks.Bookmarks;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Displays a list of the user's bookmarked artists
 */
public class ArtistBookmarksFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Bookmarks> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		TextView noBookmarks = (TextView)view.findViewById(R.id.no_content_notice);
		noBookmarks.setText("No Bookmarks");
		return view;
	}

	@Override
	public void onLoggedIn(){

	}

	@Override
	public Loader<Bookmarks> onCreateLoader(int id, Bundle args){
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Bookmarks> loader, Bookmarks data){

	}

	@Override
	public void onLoaderReset(Loader<Bookmarks> loader){
	}
}
