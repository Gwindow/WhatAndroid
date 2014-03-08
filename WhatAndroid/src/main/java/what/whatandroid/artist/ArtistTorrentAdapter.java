package what.whatandroid.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import api.torrents.ReleaseType;
import api.torrents.artist.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

/**
 * Displays the list of the Artist's torrent groups for selection
 */
public class ArtistTorrentAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
	private final LayoutInflater inflater;
	/**
	 * Callbacks to the Artist Activity so we can launch a new intent to view
	 * a selected torrent group
	 */
	private ViewTorrentCallbacks callbacks;
	/**
	 * The full list of releases being viewed, grouped by release type
	 */
	private SortedMap<ReleaseType, ArrayList<TorrentGroup>> releases;
	/**
	 * The list of release types being displayed as the parent items
	 */
	private ArrayList<ReleaseType> groups;

	/**
	 * Construct the adapter and assign the list of torrents to view
	 *
	 * @param context application context (must implement ViewTorrentCallbacks)
	 * @param objects The releases to be displayed in the view
	 */
	public ArtistTorrentAdapter(Context context, SortedMap<ReleaseType, ArrayList<TorrentGroup>> objects){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			callbacks = (ViewTorrentCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrentCallbacks");
		}
		releases = objects;
		groups = new ArrayList<ReleaseType>();
		for (Map.Entry<ReleaseType, ArrayList<TorrentGroup>> e : releases.entrySet()){
			groups.add(e.getKey());
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		return releases.get(groups.get(groupPosition)).get(childPosition);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		//Recycle views if we can
		ChildViewHolder holder;
		if (convertView != null){
			holder = (ChildViewHolder)convertView.getTag();
		}
		//We need to make a new view
		else {
			convertView = inflater.inflate(R.layout.list_artist_torrent, parent, false);
			holder = new ChildViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.album_art);
			holder.albumName = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			convertView.setTag(holder);
			convertView.setOnClickListener(this);
		}
		holder.torrentGroup = (TorrentGroup)getChild(groupPosition, childPosition);
		ImageLoader.getInstance().displayImage(holder.torrentGroup.getWikiImage(), holder.art);
		holder.albumName.setText(holder.torrentGroup.getGroupName());
		holder.year.setText(holder.torrentGroup.getGroupYear().toString());
		//TODO: Don't just serialize the array, display a comma separated list
		holder.tags.setText(holder.torrentGroup.getTags().toString());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return releases.get(groups.get(groupPosition)).size();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){
		return true;
	}

	@Override
	public Object getGroup(int groupPosition){
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount(){
		return groups.size();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
		GroupViewHolder holder;
		if (convertView != null){
			holder = (GroupViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_torrent_release_group, parent, false);
			holder = new GroupViewHolder();
			holder.groupName = (TextView)convertView.findViewById(R.id.release_category);
			convertView.setTag(holder);
		}
		holder.groupName.setText(groups.get(groupPosition).toString());
		return convertView;
	}

	@Override
	public long getGroupId(int groupPosition){
		return groupPosition;
	}

	@Override
	public boolean hasStableIds(){
		return false;
	}

	@Override
	public void onClick(View v){
		ChildViewHolder holder = (ChildViewHolder)v.getTag();
		if (holder != null){
			callbacks.viewTorrentGroup(holder.torrentGroup.getGroupId().intValue());
		}
	}

	/**
	 * View holder for the release group information
	 */
	private static class GroupViewHolder {
		public TextView groupName;
	}

	/**
	 * View holder for the Torrent Group information
	 */
	private static class ChildViewHolder {
		public TorrentGroup torrentGroup;
		public ImageView art;
		public TextView albumName, year, tags;
	}
}
