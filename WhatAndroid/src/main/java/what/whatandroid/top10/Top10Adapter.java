package what.whatandroid.top10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import api.top.Torrent;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Displays a list of the to torrents and when one is clicked requests
 * the activity to view the torrent
 */
public class Top10Adapter extends ArrayAdapter<Torrent> implements AdapterView.OnItemClickListener {
	private ViewTorrentCallbacks viewTorrent;
	private LayoutInflater inflater;

	public Top10Adapter(Context context){
		super(context, R.layout.list_torrent_notification);
		inflater = LayoutInflater.from(context);
		try {
			viewTorrent = (ViewTorrentCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_torrent_notification, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.artist_name);
			holder.edition = (TextView)convertView.findViewById(R.id.album_name);
			convertView.setTag(holder);
		}
		Torrent t = getItem(position);
		if (!t.getArtist().equalsIgnoreCase("false")){
			holder.title.setText(t.getArtist() + " - " + t.getGroupName());
		}
		else {
			holder.title.setText(t.getGroupName());
		}
		holder.edition.setText(t.getShortTitle());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Torrent t = getItem(position);
		viewTorrent.viewTorrent(t.getGroupId().intValue(), t.getTorrentId().intValue());
	}

	private static class ViewHolder {
		public TextView title, edition;
	}
}
