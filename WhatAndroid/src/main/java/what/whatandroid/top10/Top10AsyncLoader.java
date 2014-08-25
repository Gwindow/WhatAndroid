package what.whatandroid.top10;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import api.top.TopTorrents;

/**
 * Use to load the categories of top 10 torrents
 */
public class Top10AsyncLoader extends AsyncTaskLoader<TopTorrents> {
	private TopTorrents topTorrents;

	public Top10AsyncLoader(Context context){
		super(context);
	}

	@Override
	public TopTorrents loadInBackground(){
		if (topTorrents == null){
			while (true){
				topTorrents = TopTorrents.top();
				//If we get rate limited wait and retry. It's very unlikely the user has
				//used all 5 of our requests per 10s so don't wait the whole time
				if (topTorrents != null && !topTorrents.getStatus() && topTorrents.getError() != null
					&& topTorrents.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return topTorrents;
	}

	@Override
	protected void onStartLoading(){
		if (topTorrents != null){
			deliverResult(topTorrents);
		}
		else {
			forceLoad();
		}
	}
}
