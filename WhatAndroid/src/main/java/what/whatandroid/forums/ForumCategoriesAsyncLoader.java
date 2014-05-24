package what.whatandroid.forums;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.forum.categories.ForumCategories;

/**
 * Use to load the forum categories from the site
 */
public class ForumCategoriesAsyncLoader extends AsyncTaskLoader<ForumCategories> {
	private ForumCategories forumCategories;

	public ForumCategoriesAsyncLoader(Context context, Bundle args){
		super(context);
	}

	@Override
	public ForumCategories loadInBackground(){
		if (forumCategories == null){
			//Load and retry if we're rate limited
			while (true){
				forumCategories = ForumCategories.init();
				if (forumCategories != null && !forumCategories.getStatus() && forumCategories.getError() != null
					&& forumCategories.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return forumCategories;
	}

	@Override
	protected void onStartLoading(){
		if (forumCategories != null){
			deliverResult(forumCategories);
		}
		else {
			forceLoad();
		}
	}
}
