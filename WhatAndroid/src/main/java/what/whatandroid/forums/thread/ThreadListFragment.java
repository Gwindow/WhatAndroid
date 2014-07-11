package what.whatandroid.forums.thread;

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

import api.forum.thread.ForumThread;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.comments.CommentsAdapter;
import what.whatandroid.forums.ForumActivity;

/**
 * Fragment to display a list of the posts in some forum thread
 */
public class ThreadListFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<ForumThread> {
	//Used to save/restore the scroll position of the list view so we can return to the post we were viewing
	private static final String SCROLL_STATE = "what.whatandroid.threadlistfragment.SCROLL_STATE";
	private static final int LAST_POST = -2, NO_POST = -1;

	private LoadingListener<ForumThread> listener;
	private ListView list;
	private ProgressBar loadingIndicator;
	private CommentsAdapter adapter;
	private int postId = NO_POST;
	private Parcelable scrollState;

	/**
	 * Get a fragment displaying the list of posts in a thread
	 *
	 * @param thread thread id to view
	 * @param page   page of posts to display
	 */
	public static ThreadListFragment newInstance(int thread, int page){
		ThreadListFragment f = new ThreadListFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.THREAD_ID, thread);
		args.putInt(ForumActivity.PAGE, page);
		f.setArguments(args);
		return f;
	}

	/**
	 * Get a gragment displaying the list of posts on the page with postId on it
	 *
	 * @param thread thread id to view
	 * @param postId post id to load the page of
	 */
	public static ThreadListFragment newInstancePost(int thread, int postId){
		ThreadListFragment f = new ThreadListFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.THREAD_ID, thread);
		args.putInt(ForumActivity.POST_ID, postId);
		f.setArguments(args);
		return f;
	}

	public ThreadListFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//If we're coming back from a saved state they're probably looking at some other post now
		if (savedInstanceState != null){
			scrollState = savedInstanceState.getParcelable(SCROLL_STATE);
		}
		else {
			postId = getArguments().getInt(ForumActivity.POST_ID, NO_POST);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView) view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar) view.findViewById(R.id.loading_indicator);
		adapter = new CommentsAdapter(getActivity());
		list.setAdapter(adapter);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//If we were looking at some posts save the position of the one we're looking at so we can jump back to it
		if (list != null){
			outState.putParcelable(SCROLL_STATE, list.onSaveInstanceState());
		}
	}

	/**
	 * Set a listener to be called with the loaded thread data once it's loaded
	 */
	public void setListener(LoadingListener<ForumThread> listener){
		this.listener = listener;
	}

	/**
	 * Tell the fragment that it should reload the list of posts being shown
	 */
	public void reloadPosts(){
		postId = LAST_POST;
		getLoaderManager().destroyLoader(0);
		getLoaderManager().initLoader(0, getArguments(), this);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public Loader<ForumThread> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new ThreadAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<ForumThread> loader, ForumThread data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || data.getResponse() == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load page", Toast.LENGTH_LONG).show();
		}
		else {
			if (adapter.isEmpty()){
				adapter.addAll(data.getResponse().getPosts());
				adapter.notifyDataSetChanged();
				if (listener != null){
					listener.onLoadingComplete(data);
				}
				//If we're coming back to this page and were previously viewing some post index, select it
				if (scrollState != null){
					list.onRestoreInstanceState(scrollState);
				}
				//If we're jumping to the last post
				if (postId == LAST_POST){
					list.setSelection(list.getCount() - 1);
					postId = NO_POST;
				}
				//If we're jumping to a post id and it's in the range of posts for this page find it and select it
				else if (postId != NO_POST && postId >= adapter.getItem(0).getPostId()
					&& postId <= adapter.getItem(adapter.getCount() - 1).getPostId()){
					int select = 0;
					//Run through the posts and find the index corresponding to the post we want to view
					for (; postId != adapter.getItem(select).getPostId() && select < adapter.getCount(); ++select){
						;
					}
					list.setSelection(select);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<ForumThread> loader){
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
