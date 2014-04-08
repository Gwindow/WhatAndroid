package what.whatandroid.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.search.torrents.TorrentSearch;

/**
 * AsyncTaskLoader to load some page of a torrent search with the desired terms & tags
 * bundle should contain at least one of search terms or tags. If no page number is
 * passed the first page is loaded
 */
public class TorrentSearchAsyncLoader extends AsyncTaskLoader<TorrentSearch> {
	private TorrentSearch search;
	private String terms, tags;
	private int page;

	public TorrentSearchAsyncLoader(Context context, Bundle args){
		super(context);
		terms = args.getString(SearchActivity.TERMS, "");
		tags = args.getString(SearchActivity.TAGS, "");
		page = args.getInt(SearchActivity.PAGE, 1);
	}

	@Override
	public TorrentSearch loadInBackground(){
		if (search == null){
			while (true){
				search = TorrentSearch.search(terms, tags, page);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (!search.getStatus() && search.getError().equalsIgnoreCase("rate limit exceeded")){
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

	public String getTags(){
		return tags;
	}
}
