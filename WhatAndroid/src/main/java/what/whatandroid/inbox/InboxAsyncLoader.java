package what.whatandroid.inbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import api.inbox.inbox.Inbox;

/**
 * Async loader to load some page of the user's inbox
 */
public class InboxAsyncLoader extends AsyncTaskLoader<Inbox> {
	private Inbox inbox;
	private int page;

	public InboxAsyncLoader(Context context, Bundle args){
		super(context);
		page = args.getInt(InboxListFragment.PAGE);
	}

	@Override
	public Inbox loadInBackground(){
		if (inbox == null){
			while (true){
				inbox = Inbox.page(page);
				//If we get rate limited wait and retry. It's unlikely that the user has used all
				//5 of our requests per 10s so don't wait the whole timeout perioud
				if (inbox != null && !inbox.getStatus() && inbox.getError() != null
					&& inbox.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return inbox;
	}

	@Override
	protected void onStartLoading(){
		if (inbox != null){
			deliverResult(inbox);
		}
		else {
			forceLoad();
		}
	}
}
