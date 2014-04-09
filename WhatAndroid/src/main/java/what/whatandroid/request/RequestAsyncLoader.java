package what.whatandroid.request;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.comments.SimpleComment;
import api.requests.Request;
import what.whatandroid.imgloader.SmileyProcessor;

import java.util.Collections;

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
