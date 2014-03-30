package what.whatandroid.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.cli.Utils;
import api.torrents.ReleaseType;
import api.torrents.artist.Requests;
import api.torrents.artist.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Displays the list of the Artist's torrent groups for selection
 */
public class ArtistTorrentAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
	private final Context context;
	private final LayoutInflater inflater;
	/**
	 * Callbacks to the Artist Activity so we can launch a new intent to view
	 * a selected torrent group or request
	 */
	private ViewTorrentCallbacks viewTorrent;
	private ViewRequestCallbacks viewRequest;
	/**
	 * The full list of releases being viewed, grouped by release type and the artists requests
	 */
	private SortedMap<ReleaseType, ArrayList<TorrentGroup>> releases;
	private List<Requests> requests;
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
	public ArtistTorrentAdapter(Context context, SortedMap<ReleaseType, ArrayList<TorrentGroup>> objects, List<Requests> requests){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		try {
			viewTorrent = (ViewTorrentCallbacks)context;
			viewRequest = (ViewRequestCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrent & ViewRequest Callbacks");
		}
		releases = objects;
		this.requests = requests;
		groups = new ArrayList<ReleaseType>();
		for (Map.Entry<ReleaseType, ArrayList<TorrentGroup>> e : releases.entrySet()){
			groups.add(e.getKey());
		}
	}

	@Override
	public int getChildTypeCount(){
		return 2;
	}

	/**
	 * Child type of 1 is releases, 0 is torrent
	 */
	@Override
	public int getChildType(int groupPosition, int childPosition){
		//The last group is the requests
		return groupPosition < groups.size() ? 0 : 1;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		return getChildType(groupPosition, childPosition) == 0 ? releases.get(groups.get(groupPosition)).get(childPosition)
			: requests.get(childPosition);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		if (getChildType(groupPosition, childPosition) == 0){
			return getTorrentView(groupPosition, childPosition, convertView, parent);
		}
		else {
			return getRequestView(groupPosition, childPosition, convertView, parent);
		}
	}

	/**
	 * Setup a torrent view for the list
	 */
	private View getTorrentView(int groupPos, int childPos, View convert, ViewGroup parent){
		TorrentViewHolder holder;
		if (convert != null){
			holder = (TorrentViewHolder)convert.getTag();
		}
		else {
			convert = inflater.inflate(R.layout.list_artist_torrent, parent, false);
			holder = new TorrentViewHolder();
			holder.art = (ImageView)convert.findViewById(R.id.art);
			holder.spinner = (ProgressBar)convert.findViewById(R.id.loading_indicator);
			holder.listener = new ImageLoadingListener(holder.spinner);
			holder.albumName = (TextView)convert.findViewById(R.id.album_name);
			holder.year = (TextView)convert.findViewById(R.id.album_year);
			holder.tags = (TextView)convert.findViewById(R.id.album_tags);
			convert.setTag(holder);
		}
		holder.torrentGroup = (TorrentGroup)getChild(groupPos, childPos);
		String img = holder.torrentGroup.getWikiImage();
		if (SettingsActivity.imagesEnabled(context) && img != null && !img.isEmpty()){
			ImageLoader.getInstance().displayImage(holder.torrentGroup.getWikiImage(), holder.art, holder.listener);
		}
		else {
			holder.art.setVisibility(View.GONE);
			holder.spinner.setVisibility(View.GONE);
		}
		holder.albumName.setText(holder.torrentGroup.getGroupName());
		holder.year.setText(holder.torrentGroup.getGroupYear().toString());
		String tagString = holder.torrentGroup.getTags().toString();
		//Take the brackets off the tag string
		tagString = tagString.substring(tagString.indexOf('[') + 1, tagString.lastIndexOf(']'));
		holder.tags.setText(tagString);
		return convert;
	}


	/**
	 * Setup a request view for the list
	 */
	private View getRequestView(int groupPos, int childPos, View convert, ViewGroup parent){
		RequestViewHolder holder;
		if (convert != null){
			holder = (RequestViewHolder)convert.getTag();
		}
		else {
			convert = inflater.inflate(R.layout.list_request, parent, false);
			holder = new RequestViewHolder();
			holder.title = (TextView)convert.findViewById(R.id.title);
			holder.year = (TextView)convert.findViewById(R.id.year);
			holder.votes = (TextView)convert.findViewById(R.id.votes);
			holder.bounty = (TextView)convert.findViewById(R.id.bounty);
			holder.created = (TextView)convert.findViewById(R.id.created);
			convert.setTag(holder);
		}
		holder.request = (Requests)getChild(groupPos, childPos);
		holder.title.setText(holder.request.getTitle());
		holder.year.setText(holder.request.getYear().toString());
		holder.votes.setText(holder.request.getVotes().toString());
		holder.bounty.setText(Utils.toHumanReadableSize(holder.request.getBounty().longValue()));
		holder.created.setText(holder.request.getTimeAdded());
		return convert;
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return groupPosition < groups.size() ? releases.get(groups.get(groupPosition)).size()
			: requests.size();
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
		return groupPosition < groups.size() ? groups.get(groupPosition) : requests;
	}

	@Override
	public int getGroupCount(){
		return groups.size() + 1;
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
		if (groupPosition < groups.size()){
			holder.groupName.setText(groups.get(groupPosition).toString());
		}
		else {
			holder.groupName.setText("Requests");
		}
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
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
		if (getChildType(groupPosition, childPosition) == 0){
			TorrentViewHolder holder = (TorrentViewHolder)v.getTag();
			viewTorrent.viewTorrentGroup(holder.torrentGroup.getGroupId().intValue());
		}
		else {
			RequestViewHolder holder = (RequestViewHolder)v.getTag();
			viewRequest.viewRequest(holder.request.getRequestId().intValue());
		}
		return true;
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
	private static class TorrentViewHolder {
		public TorrentGroup torrentGroup;
		public ImageView art;
		public ProgressBar spinner;
		public ImageLoadingListener listener;
		public TextView albumName, year, tags;
	}

	/**
	 * View holder for Request information
	 */
	private static class RequestViewHolder {
		public Requests request;
		public TextView title, year, votes, bounty, created;
	}
}
