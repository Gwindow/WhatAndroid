package what.whatandroid.torrentgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.Artist;
import api.torrents.torrents.Edition;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewArtistCallbacks;

import java.util.List;

/**
 * Displays a list of the torrents in the group for selection
 */
public class TorrentGroupAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
	private final LayoutInflater inflater;
	/**
	 * The artists who appeared on this release
	 */
	List<Artist> artists;
	/**
	 * Callbacks to the parent activity for viewing an artist from the group
	 */
	private ViewArtistCallbacks callbacks;
	/**
	 * The list of editions being displayed
	 */
	private List<Edition> editions;

	public TorrentGroupAdapter(Context context, List<Artist> artists, List<Edition> objects){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.artists = artists;
		editions = objects;

		try {
			callbacks = (ViewArtistCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtistCallbacks");
		}
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return groupPosition == 0 ? artists.size()
			: editions.get(groupPosition - 1).getTorrents().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		return groupPosition == 0 ? artists.get(childPosition)
			: editions.get(groupPosition - 1).getTorrents().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		if (groupPosition == 0){
			return getArtistView(childPosition, isLastChild, convertView, parent);
		}
		return getTorrentView(groupPosition, childPosition, isLastChild, convertView, parent);
	}

	/**
	 * Build the artist name view to return a view showing the name of one of the contributing artists
	 */
	private View getArtistView(int childpos, boolean isLastChild, View convertView, ViewGroup parent){
		ArtistViewHolder holder = null;
		if (convertView != null){
			try {
				holder = (ArtistViewHolder)convertView.getTag();
			}
			catch (ClassCastException e){
				convertView = null;
			}
		}
		if (convertView == null){
			convertView = inflater.inflate(R.layout.fragment_torrent_artist, parent, false);
			holder = new ArtistViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.artist_name);
			holder.type = (TextView)convertView.findViewById(R.id.artist_type);
			convertView.setTag(holder);
		}
		holder.artist = artists.get(childpos);
		holder.name.setText(holder.artist.getName());
		holder.type.setText(holder.artist.getType().toString());
		return convertView;
	}

	/**
	 * Build the torrent view to return a view showing information about one of the torrents available to download
	 */
	private View getTorrentView(int grouppos, int childpos, boolean isLastChild, View convertView, ViewGroup parent){
		TorrentViewHolder holder = null;
		if (convertView != null){
			try {
				holder = (TorrentViewHolder)convertView.getTag();
			}
			catch (ClassCastException e){
				convertView = null;
			}
		}
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fragment_group_torrent, parent, false);
			holder = new TorrentViewHolder();
			holder.format = (TextView)convertView.findViewById(R.id.format);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			convertView.setTag(holder);
		}
		holder.torrent = (Torrents)getChild(grouppos, childpos);
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
		return 1 + editions.size();
	}

	@Override
	public Object getGroup(int groupPosition){
		return groupPosition == 0 ? artists : editions.get(groupPosition - 1);
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
			holder.groupName.setText(editions.get(groupPosition - 1).getEdition());
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds(){
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
		if (groupPosition == 0){
			ArtistViewHolder holder = (ArtistViewHolder)v.getTag();
			callbacks.viewArtist(holder.artist.getId().intValue());
		}
		else {
			TorrentViewHolder holder = (TorrentViewHolder)v.getTag();
		}
		return true;
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
	private static class TorrentViewHolder {
		public TextView format, size, snatches, seeders, leechers;
		public Torrents torrent;
	}

	/**
	 * View holder for the Artist information
	 */
	private static class ArtistViewHolder {
		public TextView name, type;
		public Artist artist;
	}
}
