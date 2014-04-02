package what.whatandroid.torrentgroup;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.Artist;
import api.torrents.torrents.EditionTorrents;
import api.torrents.torrents.MusicInfo;
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
	 * Fragment manager so we can show the download dialogs
	 */
	private final FragmentManager fragmentManager;
	/**
	 * The artists who appeared on this release and the string for the artist header
	 * this header text is Additional Artists if some artists are being shown in the title
	 * header, or is simply Artists on a various artists release
	 */
	private List<Artist> artists;
	private final String artistHeader;
	/**
	 * Callbacks to the parent activity for viewing an artist from the group
	 */
	private ViewArtistCallbacks callbacks;
	/**
	 * The list of editions being displayed
	 */
	private List<EditionTorrents> editions;

	/**
	 * Setup the adapter to display a list of the torrents for some torrent group and the artists if there
	 * are artists on the torrent
	 *
	 * @param musicInfo artists, or null if the torrent has none (eg. a non-music torrent)
	 * @param objects   the list of editions for the torrent group
	 */
	public TorrentGroupAdapter(Context context, FragmentManager fm, MusicInfo musicInfo, List<EditionTorrents> objects){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fragmentManager = fm;
		if (musicInfo != null){
			artists = musicInfo.getAllArtists();
		}
		editions = objects;
		artistHeader = "Artists";
		try {
			callbacks = (ViewArtistCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtistCallbacks");
		}
	}

	/**
	 * Setup the adapter to display a list of torrents for some group and the passed list of artists
	 */
	public TorrentGroupAdapter(Context context, FragmentManager fm, List<Artist> a, List<EditionTorrents> objects){
		super();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fragmentManager = fm;
		artists = a;
		editions = objects;
		artistHeader = "Additional Artists";
		try {
			callbacks = (ViewArtistCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtistCallbacks");
		}
	}

	/**
	 * Type 0 is artist view, 1 is torrent
	 */
	@Override
	public int getChildType(int groupPosition, int childPosition){
		return groupPosition == 0 && artists != null && !artists.isEmpty() ? 0 : 1;
	}

	@Override
	public int getChildTypeCount(){
		return 2;
	}

	@Override
	public int getChildrenCount(int groupPosition){
		if (artists != null && !artists.isEmpty()){
			return groupPosition == 0 ? artists.size()
				: editions.get(groupPosition - 1).getTorrents().size();
		}
		return editions.get(groupPosition).getTorrents().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition){
		if (artists != null && !artists.isEmpty()){
			return groupPosition == 0 ? artists.get(childPosition)
				: editions.get(groupPosition - 1).getTorrents().get(childPosition);
		}
		return editions.get(groupPosition).getTorrents().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		if (getChildType(groupPosition, childPosition) == 0){
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
			convertView = inflater.inflate(R.layout.list_torrent_artist, parent, false);
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
		if (convertView == null){
			convertView = inflater.inflate(R.layout.list_group_torrent, parent, false);
			holder = new TorrentViewHolder();
			holder.format = (TextView)convertView.findViewById(R.id.format);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			holder.freeleech = convertView.findViewById(R.id.freeleech_icon);
			holder.reported = convertView.findViewById(R.id.reported_icon);
			convertView.setTag(holder);
		}
		holder.torrent = (Torrents)getChild(grouppos, childpos);
		holder.format.setText(holder.torrent.getMediaFormatEncoding());
		holder.size.setText(Utils.toHumanReadableSize(holder.torrent.getSize().longValue()));
		holder.snatches.setText(holder.torrent.getSnatched().toString());
		holder.seeders.setText(holder.torrent.getSeeders().toString());
		holder.leechers.setText(holder.torrent.getLeechers().toString());

		//Hide show the freeleech & reported icons appropriately
		if (!holder.torrent.isFreeTorrent()){
			holder.freeleech.setVisibility(View.GONE);
		}
		else {
			holder.freeleech.setVisibility(View.VISIBLE);
		}
		if (!holder.torrent.isReported()){
			holder.reported.setVisibility(View.GONE);
		}
		else {
			holder.reported.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){
		return true;
	}

	@Override
	public int getGroupCount(){
		return artists != null && !artists.isEmpty() ? 1 + editions.size() : editions.size();
	}

	@Override
	public Object getGroup(int groupPosition){
		if (artists != null && !artists.isEmpty()){
			return groupPosition == 0 ? artists : editions.get(groupPosition - 1);
		}
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
		if (artists != null && !artists.isEmpty()){
			if (groupPosition == 0){
				holder.groupName.setText(artistHeader);
			}
			else {
				holder.groupName.setText(editions.get(groupPosition - 1).getEdition().toString());
			}
		}
		else {
			holder.groupName.setText(editions.get(groupPosition).getEdition().toString());
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds(){
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
		if (artists != null && !artists.isEmpty() && groupPosition == 0){
			ArtistViewHolder holder = (ArtistViewHolder)v.getTag();
			callbacks.viewArtist(holder.artist.getId().intValue());
		}
		else {
			TorrentViewHolder holder = (TorrentViewHolder)v.getTag();
			DownloadDialog dialog = DownloadDialog.newInstance(holder.torrent);
			dialog.show(fragmentManager, "DownloadDialog");
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
		public View freeleech, reported;
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
