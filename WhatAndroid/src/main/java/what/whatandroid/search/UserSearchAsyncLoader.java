package what.whatandroid.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.search.user.UserSearch;

/**
 * AsyncTaskLoader to load some page of a user search with the desired terms & tags
 * bundle should contain at least one of search terms or tags. If no page number is
 * passed the first page is loaded
 */
public class UserSearchAsyncLoader extends AsyncTaskLoader<UserSearch> {
	private UserSearch search;
	private String terms;
	private int page;

	public UserSearchAsyncLoader(Context context, Bundle args){
		super(context);
		terms = args.getString(SearchActivity.TERMS, "");
		page = args.getInt(SearchActivity.PAGE, 1);
	}

	@Override
	public UserSearch loadInBackground(){
		if (search == null){
			while (true){
				search = UserSearch.search(terms, page);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (search != null && !search.getStatus() && search.getError() != null && search.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return search;
	}

	@Override
	protected void onStartLoading(){
		if (search != null){
			deliverResult(search);
		}
		else {
			forceLoad();
		}
	}

	public String getTerms(){
		return terms;
	}
}
