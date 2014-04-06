package what.whatandroid.torrentgroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.torrents.torrents.comments.Comment;
import api.torrents.torrents.comments.TorrentComments;
import what.whatandroid.imgloader.SmileyProcessor;

/**
 * AsyncTaskLoader to load comments for some torrent, pass the torrent id
 * and the page number desired to be loaded. If the last page of comments is
 * desired then pass -1.
 * If you get back a null response you're hitting the site too frequently and should
 * restart the loader with an additional argument of RATE_LIMIT = true so
 * the loader knows to sleep for a bit
 */
public class TorrentCommentsAsyncLoader extends AsyncTaskLoader<TorrentComments> {
	public static final String RATE_LIMIT = "what.whatandroid.LOADER_RATE_LIMIT";
	private TorrentComments comments;
	private int groupId;
	private int page;
	private boolean rateLimit;

	public TorrentCommentsAsyncLoader(Context context, Bundle args){
		super(context);
		groupId = args.getInt(TorrentGroupActivity.GROUP_ID);
		page = args.getInt(TorrentCommentsFragment.COMMENTS_PAGE, -1);
		rateLimit = args.getBoolean(RATE_LIMIT, false);
	}

	@Override
	public TorrentComments loadInBackground(){
		//If we've been told we hit the rate limit then sleep for a bit
		if (rateLimit){
			try {
				Thread.sleep(2 * 1000, 0);
			}
			catch (InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
		//If we're loading the last page of comments then no page number is set. This lets us
		//mimic the site behavior of showing most recent comments first
		if (comments == null || comments.getResponse() == null){
			if (page == -1){
				comments = TorrentComments.fromId(groupId);
				page = comments.getPage();
			}
			else {
				comments = TorrentComments.fromId(groupId, page);
			}
			for (Comment c : comments.getResponse().getComments()){
				c.setBody(SmileyProcessor.smileyToEmoji(c.getBody()));
			}
		}
		return comments;
	}

	@Override
	protected void onStartLoading(){
		if (comments != null){
			deliverResult(comments);
		}
		else {
			forceLoad();
		}
	}
}
