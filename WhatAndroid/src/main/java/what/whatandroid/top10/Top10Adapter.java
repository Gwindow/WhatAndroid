package what.whatandroid.top10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import api.cli.Utils;
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
			convertView = inflater.inflate(R.layout.list_top_torrent, parent, false);
			holder = new ViewHolder();
			holder.artist = (TextView)convertView.findViewById(R.id.artist_name);
			holder.torrentName = (TextView)convertView.findViewById(R.id.torrent_name);
			holder.year = (TextView)convertView.findViewById(R.id.year);
			holder.tags = (TextView)convertView.findViewById(R.id.tags);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.data = (TextView)convertView.findViewById(R.id.data_transferred);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			convertView.setTag(holder);
		}
		Torrent t = getItem(position);
		if (!t.getArtist().equalsIgnoreCase("false")){
			holder.artist.setText(t.getArtist());
		}
		else {
			holder.artist.setVisibility(View.GONE);
		}
		holder.torrentName.setText(t.getGroupName());
		if (t.isRemastered()){
			holder.year.setText("[" + t.getYear().toString() + "] - [" + t.getShortTitle() + "]");
		}
		else {
			holder.year.setText("[" + t.getGroupYear().toString() + "] - [" + t.getShortTitle() + "]");
		}
		holder.size.setText(Utils.toHumanReadableSize(t.getSize().longValue()));
		holder.data.setText(Utils.toHumanReadableSize(t.getData().longValue()));
		holder.snatches.setText(t.getSnatched().toString());
		holder.seeders.setText(t.getSeeders().toString());
		holder.leechers.setText(t.getLeechers().toString());
		String tagString = t.getTags().toString();
		tagString = tagString.substring(tagString.indexOf('[') + 1, tagString.lastIndexOf(']'));
		holder.tags.setText(tagString);
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Torrent t = getItem(position);
		viewTorrent.viewTorrent(t.getGroupId().intValue(), t.getTorrentId().intValue());
	}

	private static class ViewHolder {
		public TextView artist, torrentName, year, tags, size, data, snatches,
			seeders, leechers;
	}
}
