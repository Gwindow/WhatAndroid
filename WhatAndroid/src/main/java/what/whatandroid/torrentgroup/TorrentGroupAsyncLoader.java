package what.whatandroid.torrentgroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.torrents.torrents.TorrentGroup;

/**
 * Use to load some torrent group from the API
 */
public class TorrentGroupAsyncLoader extends AsyncTaskLoader<TorrentGroup> {
	private TorrentGroup torrentGroup;
	private int groupId;

	public TorrentGroupAsyncLoader(Context context, Bundle args){
		super(context);
		groupId = args.getInt(TorrentGroupActivity.GROUP_ID);
	}

	@Override
	public TorrentGroup loadInBackground(){
		if (torrentGroup == null){
			torrentGroup = TorrentGroup.fromId(groupId);
			torrentGroup.getEditions();
		}
		return torrentGroup;
	}

	@Override
	protected void onStartLoading(){
		//If we've already loaded then deliver immediately
		if (torrentGroup != null){
			deliverResult(torrentGroup);
		}
		else {
			forceLoad();
		}
	}
}
