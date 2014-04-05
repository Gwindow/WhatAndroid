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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		groupId = getArguments().getInt(TorrentGroupActivity.GROUP_ID);
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		adapter = new CommentsAdapter(getActivity());
		list.addFooterView(footer);
		list.setAdapter(adapter);
		list.setOnScrollListener(this);
		//If we loaded the comments before creating the view
		if (comments != null){
			//If we're reloading the last page clear all previous comments
			if (!comments.hasNextPage()){
				adapter.clear();
			}
			adapter.addAll(comments.getResponse().getComments());
			adapter.notifyDataSetChanged();
			if (!comments.hasPreviousPage()){
				footer.setVisibility(View.GONE);
			}
		}
		return view;
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
		Bundle args = new Bundle();
		groupId = data.getId();
		args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
		getLoaderManager().initLoader(0, args, this);
	}

	@Override
	public Loader<TorrentComments> onCreateLoader(int id, Bundle args){
		return new TorrentCommentsAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<TorrentComments> loader, TorrentComments data){
		if (data.getResponse() == null){
			//If we get a null response it's because we're making too many requests
			//so re-launch the request but also have the loader sleep a bit before hitting the site
			Bundle args = new Bundle();
			args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
			args.putInt(COMMENTS_PAGE, comments.getPage() - 1);
			args.putBoolean(TorrentCommentsAsyncLoader.RATE_LIMIT, true);
			getLoaderManager().restartLoader(loader.getId(), args, this);
		}
		else {
			loadingPrev = false;
			comments = data;
			//If we loaded before creating the view
			if (adapter != null){
				//If we're reloading the last page clear all previous comments
				if (!comments.hasNextPage()){
					adapter.clear();
				}
				adapter.addAll(comments.getResponse().getComments());
				adapter.notifyDataSetChanged();
				if (!comments.hasPreviousPage()){
					footer.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<TorrentComments> loader){
	}
}