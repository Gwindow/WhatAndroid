package what.whatandroid.forums.thread;

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
	private LoadingListener<ForumThread> listener;
	private ListView list;
	private ProgressBar loadingIndicator;
	private CommentsAdapter adapter;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new CommentsAdapter(getActivity());
		list.setAdapter(adapter);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
		return view;
	}

	/**
	 * Set a listener to be called with the loaded thread data once it's loaded
	 */
	public void setListener(LoadingListener<ForumThread> listener){
		this.listener = listener;
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
				//If we're supposed to jump to a post find it and set it as selected
				int postId = getArguments().getInt(ForumActivity.POST_ID, -1);
				if (postId != -1){
					int select;
					for (select = 0; adapter.getItem(select).getPostId() != postId && select < adapter.getCount(); ++select)
						;
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
