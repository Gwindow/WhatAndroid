package what.whatandroid.forums.forum;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import api.forum.forum.Forum;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.settings.SettingsActivity;

/**
 * Fragment that displays a list of the forum threads on some page of the forum
 */
public class ForumListFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Forum> {
	//Used to save/restore our scroll position in the forum page view
	private static final String SCROLL_STATE = "what.whatandroid.forumlistfragment.SCROLL_STATE";

	private LoadingListener<Forum> listener;
	private ProgressBar loadingIndicator;
	private ListView list;
	private ForumListAdapter adapter;
	private Parcelable scrollState;

	/**
	 * Get a fragment displaying the list of posts at some page in the forum
	 *
	 * @param forum forum id to view
	 * @param page  page of posts to display
	 */
	public static ForumListFragment newInstance(int forum, int page){
		ForumListFragment f = new ForumListFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.FORUM_ID, forum);
		args.putInt(ForumActivity.PAGE, page);
		f.setArguments(args);
		return f;
	}

	public ForumListFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null){
			scrollState = savedInstanceState.getParcelable(SCROLL_STATE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		if (SettingsActivity.lightLayoutEnabled(getActivity())){
			adapter = new ForumListAdapterLight(getActivity());
		}
		else {
			adapter = new ForumListAdapterDefault(getActivity());
		}
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		//Pick the appropriate list adapter depending on our preference, if this is the first time we're
		//making the view then we just pick the adapter, if we've already got an adapter we check that
		//that type is still the one we want to show, ie. did the user go to settings and change it?
		boolean light = SettingsActivity.lightLayoutEnabled(getActivity());
		if (adapter == null || !showingRightAdapter(light)){
			if (light){
				adapter = new ForumListAdapterLight(getActivity());
			}
			else {
				adapter = new ForumListAdapterDefault(getActivity());
			}
			list.setAdapter(adapter);
			list.setOnItemClickListener(adapter);
		}
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	/**
	 * Test that we're showing the correct adapter for the light forum preference state
	 *
	 * @param light if we should be showing a light layout
	 * @return true if we're showing the right adapter, false if we aren't
	 */
	private boolean showingRightAdapter(boolean light){
		return (light && adapter instanceof ForumListAdapterLight)
			|| (!light && adapter instanceof ForumListAdapterDefault);
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if (list != null){
			outState.putParcelable(SCROLL_STATE, list.onSaveInstanceState());
		}
	}

	/**
	 * Set a listener to be called with the loaded forum data once it's loaded
	 */
	public void setListener(LoadingListener<Forum> listener){
		this.listener = listener;
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public Loader<Forum> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new ForumAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Forum> loader, Forum data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || data.getResponse() == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load page", Toast.LENGTH_LONG).show();
		}
		else {
			if (adapter.isEmpty()){
				adapter.addAll(data.getThreads());
				adapter.notifyDataSetChanged();
				if (listener != null){
					listener.onLoadingComplete(data);
				}
				if (scrollState != null){
					list.onRestoreInstanceState(scrollState);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Forum> loader){
		adapter.clear();
		adapter.notifyDataSetChanged();
	}

	/**
	 * Set the forum layout to light/default layout.
	 *
	 * @param set True if set light version.
	 */
	public void setUseLightLayout(boolean set){
		if (set){
			adapter = new ForumListAdapterLight(getActivity());
		}
		else {
			adapter = new ForumListAdapterDefault(getActivity());
		}
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		//Make sure we're loading data, or if we've finished loading just re-call onLoadFinished
		//to populate the new adapter
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}
}
