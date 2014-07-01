package what.whatandroid.forums.categories;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import api.forum.categories.ForumCategories;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment for displaying the forum categories
 */
public class ForumCategoriesFragment extends Fragment implements OnLoggedInCallback,
	LoaderManager.LoaderCallbacks<ForumCategories> {
	/**
	 * List adapter displaying all the forum information
	 */
	private ForumCategoriesListAdapter adapter;
	private ProgressBar loadingIndicator;

	public ForumCategoriesFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new ForumCategoriesListAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		loadingIndicator.setVisibility(View.VISIBLE);

		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, null, this);
		}
	}

	@Override
	public Loader<ForumCategories> onCreateLoader(int id, Bundle args){
		return new ForumCategoriesAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<ForumCategories> loader, ForumCategories data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load forum categories", Toast.LENGTH_LONG).show();
		}
		else if (adapter.isEmpty()){
			adapter.addAll(data.getCategories());
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<ForumCategories> loader){
	}
}
