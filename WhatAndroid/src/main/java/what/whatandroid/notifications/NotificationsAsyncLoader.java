package what.whatandroid.notifications;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.notifications.Notifications;

/**
 * Async loader to load some page of the user's notifications
 */
public class NotificationsAsyncLoader extends AsyncTaskLoader<Notifications> {
	private Notifications notifications;
	private int page;

	public NotificationsAsyncLoader(Context context, Bundle args){
		super(context);
		page = args.getInt(NotificationsListFragment.PAGE);
	}

	@Override
	public Notifications loadInBackground(){
		if (notifications == null){
			while (true){
				notifications = Notifications.page(page);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (notifications != null && !notifications.getStatus() && notifications.getError() != null
					&& notifications.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return notifications;
	}

	@Override
	protected void onStartLoading(){
		if (notifications != null){
			deliverResult(notifications);
		}
		else {
			forceLoad();
		}
	}
}
