package what.whatandroid.request;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import api.cli.Utils;
import api.requests.TopContributor;
import api.torrents.torrents.Artist;
import api.torrents.torrents.MusicInfo;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewArtistCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;

import java.util.List;

/**
 * Adapter for the expandable list view in the Request fragment that shows
 * the artists and top contributors
 */
public class RequestAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
	private final LayoutInflater inflater;
	List<Artist> artists;
	List<TopContributor> topContributors;
	/**
	 * Callbacks to go view an artist or user in the list
	 */
	private ViewArtistCallbacks viewArtist;
	private ViewUserCallbacks viewUser;

	public RequestAdapter(Context context, MusicInfo info, List<TopContributor> contributors){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		artists = info.getAllArtists();
		topContributors = contributors;
		try {
			viewArtist = (ViewArtistCallbacks)context;
			viewUser = (ViewUserCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtist & ViewUser Callbacks");
		}
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return groupPosition == 0 ? artists.size() : topContributors.size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		return groupPosition == 0 ? artists.get(childPosition) : topContributors.get(childPosition);
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
			convertView = inflater.inflate(R.layout.list_torrent_artist, parent, false);
			holder = new ChildViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.artist_name);
			holder.type = (TextView)convertView.findViewById(R.id.artist_type);
			convertView.setTag(holder);
		}
		if (groupPosition == 0){
			holder.name.setText(artists.get(childPosition).getName());
			holder.type.setText(artists.get(childPosition).getType().toString());
		}
		else {
			holder.name.setText(topContributors.get(childPosition).getUserName());
			holder.type.setText(Utils.toHumanReadableSize(topContributors.get(childPosition).getBounty().longValue()));
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){
		return true;
	}

	@Override
	public int getGroupCount(){
		return 2;
	}

	@Override
	public Object getGroup(int groupPosition){
		return groupPosition == 0 ? artists : topContributors;
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
		if (groupPosition == 0){
			holder.groupName.setText("Artists");
		}
		else {
			holder.groupName.setText("Top Contributors");
		}
		return convertView;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
		if (groupPosition == 0){
			Artist a = (Artist)getChild(groupPosition, childPosition);
			if (a != null){
				viewArtist.viewArtist(a.getId().intValue());
			}
		}
		else {
			TopContributor t = (TopContributor)getChild(groupPosition, childPosition);
			if (t != null){
				viewUser.viewUser(t.getUserId().intValue());
			}
		}
		return true;
	}

	@Override
	public boolean hasStableIds(){
		return false;
	}

	private static class GroupViewHolder {
		public TextView groupName;
	}

	/**
	 * For artists the type is the type of appearance, for top contributors we show
	 * their contribution level
	 */
	private static class ChildViewHolder {
		public TextView name, type;
	}
}
