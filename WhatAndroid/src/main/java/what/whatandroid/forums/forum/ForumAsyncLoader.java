package what.whatandroid.forums.forum;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.forum.forum.Forum;
import what.whatandroid.forums.ForumActivity;

/**
 * Async loader to load some page of a forum
 */
public class ForumAsyncLoader extends AsyncTaskLoader<Forum> {
	private Forum forum;
	private int page, forumId;

	public ForumAsyncLoader(Context context, Bundle args){
		super(context);
		page = args.getInt(ForumActivity.PAGE);
		forumId = args.getInt(ForumActivity.FORUM_ID);
	}

	@Override
	public Forum loadInBackground(){
		if (forum == null){
			while (true){
				forum = Forum.forum(forumId, page);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (forum != null && !forum.getStatus() && forum.getError() != null
					&& forum.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return forum;
	}

	@Override
	protected void onStartLoading(){
		if (forum != null){
			deliverResult(forum);
		}
		else {
			forceLoad();
		}
	}
}
