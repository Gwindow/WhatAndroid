package what.whatandroid.torrentgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.TorrentFile;
import what.whatandroid.R;

/**
 * Adapter for displaying a list of the files in the torrent
 */
public class TorrentFilesAdapter extends ArrayAdapter<TorrentFile> {
	private final LayoutInflater inflater;

	public TorrentFilesAdapter(Context context){
		super(context, R.layout.list_torrent_file);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_torrent_file, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			convertView.setTag(holder);
		}
		TorrentFile file = getItem(position);
		holder.name.setText(file.getFilename());
		holder.size.setText(Utils.toHumanReadableSize(file.getSize()));
		return convertView;
	}

	private static class ViewHolder {
		public TextView name, size;
	}
}
