package what.whatandroid.bookmarks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.bookmarks.Bookmarks;

/**
 * Loads the desired bookmarks (torrents or artists) and handles rate limiting
 */
public class BookmarksAsyncLoader extends AsyncTaskLoader<Bookmarks> {
	/**
	 * Params to send via bundle to tell us what type of bookmarks to load
	 */
	public static String BOOKMARK_TYPE = "what.whatandroid.BOOKMARK_TYPE";
	public static int TORRENTS = 0, ARTISTS = 1;
	private Bookmarks bookmarks;
	private int type;

	public BookmarksAsyncLoader(Context context, Bundle args){
		super(context);
		type = args.getInt(BOOKMARK_TYPE, 0);
	}

	@Override
	public Bookmarks loadInBackground(){
		if (bookmarks == null){
			while (true){
				if (type == TORRENTS){
					bookmarks = Bookmarks.torrents();
				}
				else {
					bookmarks = Bookmarks.artists();
				}
				if (bookmarks != null && !bookmarks.getStatus() && bookmarks.getError() != null
					&& bookmarks.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return bookmarks;
	}

	@Override
	protected void onStartLoading(){
		if (bookmarks != null){
			deliverResult(bookmarks);
		}
		else {
			forceLoad();
		}
	}
}
