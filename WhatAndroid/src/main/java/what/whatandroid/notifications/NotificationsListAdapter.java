package what.whatandroid.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.cli.Utils;
import api.notifications.Torrent;
import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.imgloader.ImageLoadFailTracker;
import what.whatandroid.settings.SettingsActivity;

/**
 * Displays a list of the user's torrent notifications
 */
public class NotificationsListAdapter extends ArrayAdapter<Torrent> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private final ViewTorrentCallbacks viewTorrent;
	private ImageLoadFailTracker imageFailTracker;
	private boolean imagesEnabled;

	public NotificationsListAdapter(Context context){
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
			convertView = inflater.inflate(R.layout.list_torrent_notification, parent, false);
			holder = new ViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.art);
			holder.spinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.artContainer = convertView.findViewById(R.id.art_container);
			holder.artist = (TextView)convertView.findViewById(R.id.artist_name);
			holder.title = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			holder.size = (TextView)convertView.findViewById(R.id.size);
			holder.snatches = (TextView)convertView.findViewById(R.id.snatches);
			holder.seeders = (TextView)convertView.findViewById(R.id.seeders);
			holder.leechers = (TextView)convertView.findViewById(R.id.leechers);
			convertView.setTag(holder);
		}
		Torrent t = getItem(position);
		String coverUrl = t.getWikiImage();
		if (!imagesEnabled) {
			holder.artContainer.setVisibility(View.GONE);
		} else {
			holder.artContainer.setVisibility(View.VISIBLE);
			WhatApplication.loadImage(inflater.getContext(), coverUrl, holder.art, holder.spinner, imageFailTracker, null);
		}
		holder.artist.setText(t.getGroupName());
		holder.title.setText(t.getMediaFormatEncoding());
		holder.year.setText(t.getEdition());
		holder.tags.setText(t.getTorrentTags().replace(" ", ", ").replace('_', '.'));
		holder.size.setText(Utils.toHumanReadableSize(t.getSize().longValue()));
		holder.snatches.setText(t.getSnatched().toString());
		holder.seeders.setText(t.getSeeders().toString());
		holder.leechers.setText(t.getLeechers().toString());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		viewTorrent.viewTorrent(-1, getItem(position).getTorrentId().intValue());
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public ImageView art;
		public ProgressBar spinner;
		public View artContainer;
		public TextView artist, title, year, tags, size, snatches, seeders, leechers;
	}
}
