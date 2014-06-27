package what.whatandroid.request;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Collections;

import api.comments.SimpleComment;
import api.index.Index;
import api.requests.Request;
import api.soup.MySoup;
import api.util.CouldNotLoadException;
import what.whatandroid.comments.SmileyProcessor;

/**
 * AsyncLoader to load a request from id and a specific page of request comments if desired.
 * If no page number is passed the most recent page of comments is loaded
 */
public class RequestAsyncLoader extends AsyncTaskLoader<Request> {
	private Request request;
	private int requestId, page;

	public RequestAsyncLoader(Context context, Bundle args){
		super(context);
		requestId = args.getInt(RequestActivity.REQUEST_ID);
		page = args.getInt(RequestCommentsFragment.COMMENTS_PAGE, -1);
	}

	@Override
	public Request loadInBackground(){
		if (request == null){
			while (true){
				if (page == -1){
					//Reload the user's index as well to give them up to date information on the effect their vote
					//will have on their ratio. If we fail to load the index then we'll likely also fail to load the request
					if (!refreshIndex()){
						return null;
					}
					request = Request.fromId(requestId);
				}
				else {
					request = Request.fromId(requestId, page);
				}
				if (request != null && !request.getStatus() && request.getError() != null && request.getError().equalsIgnoreCase("rate limit exceeded")){
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException e){
						Thread.currentThread().interrupt();
					}
				}
				else {
					break;
				}
			}
			if (request != null && request.getStatus()){
				//Sort the comments to have newest ones at the top
				Collections.sort(request.getResponse().getComments(),
					Collections.reverseOrder(new SimpleComment.DateComparator()));
				for (SimpleComment c : request.getResponse().getComments()){
					c.setBody(SmileyProcessor.smileyToEmoji(c.getBody()));
				}
			}
		}
		return request;
	}

	private boolean refreshIndex(){
		//Also handle the case where we've been rate limited
		while (true){
			try {
				MySoup.loadIndex();
			}
			catch (CouldNotLoadException e){
				return false;
			}
			Index index = MySoup.getIndex();
			if (index != null && !index.getStatus() && index.getError() != null && index.getError().equalsIgnoreCase("rate limit exceeded")){
				try {
					Thread.sleep(3000);
				}
				catch (InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
			else {
				break;
			}
		}
		return MySoup.getIndex() != null && MySoup.getIndex().getStatus();
	}

	@Override
	protected void onStartLoading(){
		if (request != null){
			deliverResult(request);
		}
		else {
			forceLoad();
		}
	}
}
