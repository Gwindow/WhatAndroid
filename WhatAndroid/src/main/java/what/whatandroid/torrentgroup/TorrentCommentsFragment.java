package what.whatandroid.torrentgroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import api.soup.MySoup;
import api.torrents.torrents.TorrentGroup;
import api.torrents.torrents.comments.TorrentComments;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.comments.CommentsAdapter;

/**
 * A fragment for displaying a listing of user comments
 */
public class TorrentCommentsFragment extends Fragment
	implements LoadingListener<TorrentGroup>, LoaderManager.LoaderCallbacks<TorrentComments>, AbsListView.OnScrollListener {

	public static final String COMMENTS_PAGE = "what.whatandroid.TORRENT_COMMENTS_PAGE";
	private TorrentComments comments;
	private int groupId;
	private ListView list;
	private CommentsAdapter adapter;
	private View footer;
	private TextView noComments;
	private boolean loadingPrev;

	public static TorrentCommentsFragment newInstance(int groupId){
		TorrentCommentsFragment f = new TorrentCommentsFragment();
		Bundle args = new Bundle();
		args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
		f.setArguments(args);
		return f;
	}

	public TorrentCommentsFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		groupId = getArguments().getInt(TorrentGroupActivity.GROUP_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		noComments = (TextView)view.findViewById(R.id.no_content_notice);
		noComments.setText("No comments");
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		adapter = new CommentsAdapter(getActivity());
		list.addFooterView(footer);
		list.setAdapter(adapter);
		list.setOnScrollListener(this);

		if (MySoup.isLoggedIn()){
			Bundle args = new Bundle();
			args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
			getLoaderManager().initLoader(0, args, this);
		}
		return view;
	}

	private void updateComments(){
		//If we're reloading the last page clear all previous comments
		if (!comments.hasNextPage()){
			adapter.clear();
			//If this is the first shown page of comments and it's empty show the no comments message
			if (comments.getResponse().getComments().isEmpty()){
				noComments.setVisibility(View.VISIBLE);
			}
		}
		adapter.addAll(comments.getResponse().getComments());
		adapter.notifyDataSetChanged();
		if (!comments.hasPreviousPage()){
			footer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//We hide the footer when we load the final item
		if (comments != null && comments.hasPreviousPage() && !loadingPrev && firstVisibleItem + visibleItemCount + 5 >= totalItemCount){
			loadingPrev = true;
			Bundle args = new Bundle();
			args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
			args.putInt(COMMENTS_PAGE, comments.getPage() - 1);
			//The first page of comments is loaded by loader 0 so loader ids are really page + 1
			getLoaderManager().initLoader(comments.getPage(), args, this);
		}
	}

	@Override
	public void onLoadingComplete(TorrentGroup data){
		groupId = data.getId();
		//Keep the group id argument up to date
		getArguments().putInt(TorrentGroupActivity.GROUP_ID, groupId);
		if (isAdded() && comments == null){
			Bundle args = new Bundle();
			args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
			getLoaderManager().initLoader(0, args, this);
		}
	}

	@Override
	public Loader<TorrentComments> onCreateLoader(int id, Bundle args){
		return new TorrentCommentsAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<TorrentComments> loader, TorrentComments data){
		loadingPrev = false;
		comments = data;
		if (isAdded()){
			updateComments();
		}
		//If we just loaded the first page start loading the next page too since they're pretty small pages
		if (!comments.hasNextPage() && comments.hasPreviousPage() && !loadingPrev){
			loadingPrev = true;
			Bundle args = new Bundle();
			args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
			args.putInt(COMMENTS_PAGE, comments.getPage() - 1);
			//The first page of comments is loaded by loader 0 so loader ids are really page + 1
			getLoaderManager().initLoader(comments.getPage(), args, this);
		}
	}

	@Override
	public void onLoaderReset(Loader<TorrentComments> loader){
	}
}