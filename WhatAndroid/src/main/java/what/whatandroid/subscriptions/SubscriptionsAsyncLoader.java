package what.whatandroid.subscriptions;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import api.subscriptions.Subscriptions;

/**
 * Async loader to load a user's subscriptions information
 */
public class SubscriptionsAsyncLoader extends AsyncTaskLoader<Subscriptions> {
	private Subscriptions subscriptions;
	private boolean showAll;

	public SubscriptionsAsyncLoader(Context context, Bundle args){
		super(context);
		showAll = args.getBoolean(SubscriptionsFragment.SHOW_ALL, false);
	}

	@Override
	public Subscriptions loadInBackground(){
		if (subscriptions == null){
			//Load and retry if we're rate limited
			while (true){
				subscriptions = Subscriptions.init(showAll);
				if (subscriptions != null && !subscriptions.getStatus() && subscriptions.getError() != null
					&& subscriptions.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return subscriptions;
	}

	@Override
	protected void onStartLoading(){
		if (subscriptions != null){
			deliverResult(subscriptions);
		}
		else {
			forceLoad();
		}
	}
}
