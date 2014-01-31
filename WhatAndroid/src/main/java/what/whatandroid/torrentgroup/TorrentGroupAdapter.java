package what.whatandroid.torrentgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;

import java.util.List;

/**
 * Displays a list of the torrents in the group for selection
 */
public class TorrentGroupAdapter extends ArrayAdapter<Torrents> implements View.OnClickListener {
	private final LayoutInflater inflater;
	private final int resource;

	public TorrentGroupAdapter(Context context, int res, List<Torrents> objects){
		super(context, res, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource = res;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//Recycle views if we can
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(resource, parent, false);
			convertView.setOnClickListener(this);
			holder = new ViewHolder();
			holder.format = (TextView)convertView.findViewById(R.id.format);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			convertView.setTag(holder);
		}
		holder.torrent = getItem(position);
		holder.format.setText(holder.torrent.getFormat() + " / " + holder.torrent.getEncoding());
		holder.size.setText(Utils.toHumanReadableSize(holder.torrent.getSize().longValue()));
		holder.snatches.setText(holder.torrent.getSnatched().toString());
		holder.seeders.setText(holder.torrent.getSeeders().toString());
		holder.leechers.setText(holder.torrent.getLeechers().toString());
		return convertView;
	}

	@Override
	public void onClick(View v){

	}

	/**
	 * View holder for the Torrent information
	 */
	private static class ViewHolder {
		TextView format, size, snatches, seeders, leechers;
		Torrents torrent;
	}
}
