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
				while (true){
					Torrent t = Torrent.fromId(torrentId);
					//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
					//requests per 10s so don't wait the whole time initially
					if (!t.getStatus() && t.getError() != null && t.getError().equalsIgnoreCase("rate limit exceeded")){
						try {
							Thread.sleep(3000);
						}
						catch (InterruptedException e){
							Thread.currentThread().interrupt();
						}
					}
					else if (t == null || !t.getStatus()){
						return null;
					}
					else {
						groupId = t.getGroup().getId().intValue();
						break;
					}
				}
			}
			//Load the torrent group and retry if we fail
			while (true){
				torrentGroup = TorrentGroup.fromId(groupId);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (torrentGroup != null && !torrentGroup.getStatus() && torrentGroup.getError() != null
					&& torrentGroup.getError().equalsIgnoreCase("rate limit exceeded")){
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
			if (torrentGroup != null && torrentGroup.getStatus()){
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
