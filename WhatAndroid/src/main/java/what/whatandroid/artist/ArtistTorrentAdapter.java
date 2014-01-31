package what.whatandroid.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import api.torrents.artist.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

import java.util.List;

/**
 * Displays the list of the Artist's torrent groups for selection
 */
public class ArtistTorrentAdapter extends ArrayAdapter<TorrentGroup> implements View.OnClickListener {
	private final LayoutInflater inflater;
	private final int resource;
	/**
	 * Callbacks to the Artist Activity so we can launch a new intent to view
	 * a selected torrent group
	 */
	private ViewTorrentCallbacks callbacks;

	/**
	 * Construct the adapter and assign the list of torrents to view
	 *
	 * @param context application context (must implement ViewTorrentCallbacks)
	 * @param res     the view to inflate
	 * @param objects the objects to display
	 */
	public ArtistTorrentAdapter(Context context, int res, List<TorrentGroup> objects){
		super(context, res, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource = res;
		try {
			callbacks = (ViewTorrentCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//Recycle views if we can
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		//We need to make a new view
		else {
			convertView = inflater.inflate(resource, parent, false);
			convertView.setOnClickListener(this);
			holder = new ViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.album_art);
			holder.albumName = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			convertView.setTag(holder);
		}
		holder.torrentGroup = getItem(position);
		ImageLoader.getInstance().displayImage(holder.torrentGroup.getWikiImage(), holder.art);
		holder.albumName.setText(holder.torrentGroup.getGroupName());
		holder.year.setText(holder.torrentGroup.getGroupYear().toString());
		//TODO: Later clip this to only show the first 4 or so tags
		holder.tags.setText(holder.torrentGroup.getTags().toString());
		return convertView;
	}

	@Override
	public void onClick(View v){
		ViewHolder holder = (ViewHolder)v.getTag();
		callbacks.viewTorrentGroup(holder.torrentGroup.getGroupId().intValue());
	}

	/**
	 * View holder for the Torrent group information
	 */
	private static class ViewHolder {
		public TorrentGroup torrentGroup;
		public ImageView art;
		public TextView albumName, year, tags;
	}
}
