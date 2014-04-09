package what.whatandroid.request;

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
import android.widget.Toast;
import api.requests.Request;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.comments.CommentsAdapter;

/**
 * Fragment for displaying a list of comments on a request. The first page of comments to
 * show comes from the RequestFragment since the comments and request api responses
 * aren't separate.
 */
public class RequestCommentsFragment extends Fragment implements LoadingListener<Request>,
	LoaderManager.LoaderCallbacks<Request>, AbsListView.OnScrollListener {

	public static final String COMMENTS_PAGE = "what.whatandroid.REQUEST_COMMENTS_PAGE";
	private Request request;
	private CommentsAdapter adapter;
	private View footer;
	private TextView noComments;
	private boolean loadingPrev;

	public RequestCommentsFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		noComments = (TextView)view.findViewById(R.id.no_content_notice);
		noComments.setText("No comments");
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		adapter = new CommentsAdapter(getActivity());
		list.addFooterView(footer);
		list.setAdapter(adapter);
		list.setOnScrollListener(this);
		return view;
	}

	private void updateComments(){
		//If we're reloading the first page then clear all previous comments
		if (!request.getResponse().hasNextPage()){
			adapter.clear();
			//If this is the first page of comments and it's empty show the no comments text
			if (request.getResponse().getComments().isEmpty()){
				noComments.setVisibility(View.VISIBLE);
			}
		}
		adapter.addAll(request.getResponse().getComments());
		adapter.notifyDataSetChanged();
		if (!request.getResponse().hasPreviousPage()){
			footer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		if (request != null && request.getResponse().hasPreviousPage() && !loadingPrev && firstVisibleItem + visibleItemCount + 5 >= totalItemCount){
			loadingPrev = true;
			Bundle args = new Bundle();
			args.putInt(RequestActivity.REQUEST_ID, request.getResponse().getRequestId().intValue());
			args.putInt(COMMENTS_PAGE, request.getResponse().getCommentPage().intValue() - 1);
			//The first page of comments is loaded by loader 0 so loader ids are really page + 1
			getLoaderManager().initLoader(request.getResponse().getCommentPage().intValue(), args, this);
		}
	}

	private void onCommentsLoaded(Request data){
		loadingPrev = false;
		//Display any comments and start loading next page if there is one with our own loader
		request = data;
		if (request == null || !request.getStatus()){
			Toast.makeText(getActivity(), "Could not load comments", Toast.LENGTH_LONG).show();
			footer.setVisibility(View.GONE);
			return;
		}
		if (isAdded()){
			updateComments();
		}
		//If we just loaded the first page start loading the next one too since they're pretty small
		if (!request.getResponse().hasNextPage() && request.getResponse().hasNextPage() && !loadingPrev){
			loadingPrev = true;
			Bundle args = new Bundle();
			args.putInt(RequestActivity.REQUEST_ID, request.getResponse().getRequestId().intValue());
			args.putInt(COMMENTS_PAGE, request.getResponse().getCommentPage().intValue() - 1);
			//The first page of comments is loaded by loader 0 so loader ids are page + 1
			getLoaderManager().initLoader(request.getResponse().getCommentPage().intValue(), args, this);
		}
	}

	@Override
	public void onLoadingComplete(Request data){
		onCommentsLoaded(data);
	}

	@Override
	public Loader<Request> onCreateLoader(int id, Bundle args){
		return new RequestAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Request> loader, Request data){
		onCommentsLoaded(data);
	}

	@Override
	public void onLoaderReset(Loader<Request> loader){
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
	}
}
