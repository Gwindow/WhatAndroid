package what.whatandroid.torrentgroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.torrents.torrents.Torrent;
import api.torrents.torrents.TorrentGroup;

/**
 * Use to load some torrent group from the API, must pass at least one of: group id
 * to load or torrent id of torrent inside the group to load
 */
public class TorrentGroupAsyncLoader extends AsyncTaskLoader<TorrentGroup> {
	private TorrentGroup torrentGroup;
	private int groupId, torrentId;

	public TorrentGroupAsyncLoader(Context context, Bundle args){
		super(context);
		groupId = args.getInt(TorrentGroupActivity.GROUP_ID);
		torrentId = args.getInt(TorrentGroupActivity.TORRENT_ID);
	}

	@Override
	public TorrentGroup loadInBackground(){
		if (torrentGroup == null){
			//If we're only given a torrent id to load we need to load the torrent to get the groupId and then
			//we can load the group
			if (groupId == -1){
				Torrent t = Torrent.fromId(torrentId);
				groupId = t.getGroup().getId().intValue();
			}
			torrentGroup = TorrentGroup.fromId(groupId);
			if (torrentGroup.getResponse() != null){
				torrentGroup.getEditions();
			}
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
