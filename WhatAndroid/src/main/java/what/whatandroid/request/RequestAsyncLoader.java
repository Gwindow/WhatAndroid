package what.whatandroid.request;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.requests.Request;

/**
 * AsyncLoader to load a request from id
 */
public class RequestAsyncLoader extends AsyncTaskLoader<Request> {
	private Request request;
	private int requestId;

	public RequestAsyncLoader(Context context, Bundle args){
		super(context);
		requestId = args.getInt(RequestActivity.REQUEST_ID);
	}

	@Override
	public Request loadInBackground(){
		if (request == null){
			while (true){
				request = Request.fromId(requestId);
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
