package what.whatandroid.torrentgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.Edition;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;

import java.util.List;

/**
 * Displays a list of the torrents in the group for selection
 */
public class TorrentGroupAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
	private final LayoutInflater inflater;
	/**
	 * The list of editions being displayed
	 */
	private List<Edition> editions;

	public TorrentGroupAdapter(Context context, List<Edition> objects){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		editions = objects;
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return editions.get(groupPosition).getTorrents().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		return editions.get(groupPosition).getTorrents().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		ChildViewHolder holder;
		if (convertView != null){
			holder = (ChildViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.fragment_group_torrent, parent, false);
			convertView.setOnClickListener(this);
			holder = new ChildViewHolder();
			holder.format = (TextView)convertView.findViewById(R.id.format);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			convertView.setTag(holder);
		}
		holder.torrent = (Torrents)getChild(groupPosition, childPosition);
		holder.format.setText(holder.torrent.getMediaFormatEncoding());
		holder.size.setText(Utils.toHumanReadableSize(holder.torrent.getSize().longValue()));
		holder.snatches.setText(holder.torrent.getSnatched().toString());
		holder.seeders.setText(holder.torrent.getSeeders().toString());
		holder.leechers.setText(holder.torrent.getLeechers().toString());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){
		return true;
	}

	@Override
	public int getGroupCount(){
		return editions.size();
	}

	@Override
	public Object getGroup(int groupPosition){
		return editions.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition){
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
		GroupViewHolder holder;
		if (convertView != null){
			holder = (GroupViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_group, parent, false);
			holder = new GroupViewHolder();
			holder.groupName = (TextView)convertView.findViewById(R.id.group_category);
			convertView.setTag(holder);
		}
		holder.groupName.setText(editions.get(groupPosition).getEdition());
		return convertView;
	}

	@Override
	public boolean hasStableIds(){
		return false;
	}

	@Override
	public void onClick(View v){

	}

	/**
	 * View holder for the group information
	 */
	private static class GroupViewHolder {
		public TextView groupName;
	}

	/**
	 * View holder for the Torrent information
	 */
	private static class ChildViewHolder {
		TextView format, size, snatches, seeders, leechers;
		Torrents torrent;
	}
}
