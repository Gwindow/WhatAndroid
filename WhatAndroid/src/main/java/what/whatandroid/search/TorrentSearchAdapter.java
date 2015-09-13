package what.whatandroid.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import api.search.torrents.TorrentGroup;
import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.imgloader.ImageLoadFailTracker;
import what.whatandroid.settings.SettingsActivity;

/**
 * Adapter for viewing list of torrent search results
 */
public class TorrentSearchAdapter extends ArrayAdapter<TorrentGroup> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	/**
	 * Callbacks to view the selected torrent group
	 */
	private ViewTorrentCallbacks viewTorrent;
	private ImageLoadFailTracker imageFailTracker;
	private boolean imagesEnabled;

	/**
	 * Construct the empty adapter. A new search can be set to be viewed in the adapter by
	 * calling viewSearch
	 */
	public TorrentSearchAdapter(Context context){
		super(context, R.layout.list_torrent_search);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imagesEnabled = SettingsActivity.imagesEnabled(context);
		imageFailTracker = new ImageLoadFailTracker();
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
			convertView = inflater.inflate(R.layout.list_torrent_search, parent, false);
			holder = new ViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.art);
			holder.spinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.artContainer = convertView.findViewById(R.id.art_container);
			holder.artist = (TextView)convertView.findViewById(R.id.artist_name);
			holder.title = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			convertView.setTag(holder);
		}
		TorrentGroup group = getItem(position);
		String coverUrl = group.getCover();

        if (!imagesEnabled) holder.artContainer.setVisibility(View.GONE);
        else {
            holder.artContainer.setVisibility(View.VISIBLE);
            WhatApplication.loadImage(getContext(), coverUrl, holder.art, holder.spinner, imageFailTracker, null);
        }

		if (group.getArtist() != null){
			holder.artist.setText(group.getArtist());
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(group.getGroupName());
		}
		else {
			holder.artist.setText(group.getGroupName());
			holder.title.setVisibility(View.GONE);
		}
		if (group.getReleaseType() != null && group.getGroupYear() != null){
			holder.year.setText(group.getReleaseType() + " [" + group.getGroupYear() + "]");
		}
		else {
			holder.year.setVisibility(View.GONE);
		}
		String tagString = group.getTags().toString();
		//Remove the brackets from the tag string
		tagString = tagString.substring(tagString.indexOf('[') + 1, tagString.lastIndexOf(']'));
		holder.tags.setText(tagString);
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Clicking the footer gives us an out of bounds click event so subtract 1 to account for this
		if (position - 1 < getCount()){
			viewTorrent.viewTorrentGroup(getItem(position - 1).getGroupId().intValue());
		}
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public ImageView art;
		public ProgressBar spinner;
		public View artContainer;
		public TextView artist, title, year, tags;
	}
}
