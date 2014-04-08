package what.whatandroid.torrentgroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.comments.SimpleComment;
import api.torrents.torrents.comments.TorrentComments;
import what.whatandroid.imgloader.SmileyProcessor;

import java.util.Collections;

/**
 * AsyncTaskLoader to load comments for some torrent, pass the torrent id
 * and the page number desired to be loaded. If the last page of comments is
 * desired then pass -1.
 * If you get back a null response you're hitting the site too frequently and should
 * restart the loader with an additional argument of RATE_LIMIT = true so
 * the loader knows to sleep for a bit
 */
public class TorrentCommentsAsyncLoader extends AsyncTaskLoader<TorrentComments> {
	private TorrentComments comments;
	private int groupId, page;

	public TorrentCommentsAsyncLoader(Context context, Bundle args){
		super(context);
		groupId = args.getInt(TorrentGroupActivity.GROUP_ID);
		page = args.getInt(TorrentCommentsFragment.COMMENTS_PAGE, -1);
	}

	@Override
	public TorrentComments loadInBackground(){
		if (comments == null || comments.getResponse() == null){
			while (true){
				//If we're loading the last page of comments then no page number is set. This lets us
				//mimic the site behavior of showing most recent comments first
				if (page == -1){
					comments = TorrentComments.fromId(groupId);
					if (comments.getStatus()){
						page = comments.getPage();
					}
				}
				else {
					comments = TorrentComments.fromId(groupId, page);
				}
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (!comments.getStatus() && comments.getError().equalsIgnoreCase("rate limit exceeded")){
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
			//Sort the comments to have newest ones at the top
			Collections.sort(comments.getResponse().getComments(),
				Collections.reverseOrder(new SimpleComment.DateComparator()));
			for (SimpleComment c : comments.getResponse().getComments()){
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
