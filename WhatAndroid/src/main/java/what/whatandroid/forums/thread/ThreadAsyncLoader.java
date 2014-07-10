package what.whatandroid.forums.thread;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import api.forum.thread.ForumThread;
import what.whatandroid.forums.ForumActivity;

/**
 * Async loader to load some page of a forum thread
 */
public class ThreadAsyncLoader extends AsyncTaskLoader<ForumThread> {
	private ForumThread thread;
	private int threadId, page, postId;

	public ThreadAsyncLoader(Context context, Bundle args){
		super(context);
		threadId = args.getInt(ForumActivity.THREAD_ID);
		page = args.getInt(ForumActivity.PAGE, -1);
		postId = args.getInt(ForumActivity.POST_ID, -1);
	}

	@Override
	public ForumThread loadInBackground(){
		if (thread == null){
			while (true){
				if (page != -1){
					thread = ForumThread.thread(threadId, page);
				}
				else {
					thread = ForumThread.threadAtPost(threadId, postId);
				}
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (thread != null && !thread.getStatus() && thread.getError() != null
					&& thread.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return thread;
	}

	@Override
	protected void onStartLoading(){
		if (thread != null){
			deliverResult(thread);
		}
		else {
			forceLoad();
		}
	}

	@Override
	protected void onReset(){
		super.onReset();
		thread = null;
	}
}
