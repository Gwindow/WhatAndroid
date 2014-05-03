package what.whatandroid.bookmarks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.bookmarks.TorrentGroup;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Displays a list of the user's torrent bookmarks
 */
public class TorrentBookmarkAdapter extends ArrayAdapter<TorrentGroup> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private ViewTorrentCallbacks viewTorrent;
	private BookmarksChangedListener listener;

	public TorrentBookmarkAdapter(Context context, BookmarksChangedListener listener){
		super(context, R.layout.list_torrent_bookmark);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listener = listener;
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
			convertView = inflater.inflate(R.layout.list_torrent_bookmark, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.year = (TextView)convertView.findViewById(R.id.year);
			holder.tags = (TextView)convertView.findViewById(R.id.tags);
			holder.removeBookmark = (ImageButton)convertView.findViewById(R.id.remove_bookmark);
			convertView.setTag(holder);
		}
		final TorrentGroup group = getItem(position);
		holder.title.setText(group.getName());
		holder.year.setText(group.getYear().toString());
		holder.tags.setText(group.getTagList().replace('_', '.').replace(" ", ", "));
		holder.removeBookmark.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				new RemoveBookmarkTask().execute(group);
				remove(group);
				listener.bookmarksChanged();
			}
		});
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		viewTorrent.viewTorrentGroup(getItem(position).getId().intValue());
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public TextView title, year, tags;
		public ImageButton removeBookmark;
	}

	/**
	 * Async task to unbookmark torrents
	 */
	private class RemoveBookmarkTask extends AsyncTask<TorrentGroup, Void, Boolean> {
		@Override
		protected Boolean doInBackground(TorrentGroup... params){
			TorrentGroup group = params[0];
			return api.torrents.torrents.TorrentGroup.removeBookmark(group.getId().intValue());
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (!status){
				Toast.makeText(getContext(), "Could not remove bookmark", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
