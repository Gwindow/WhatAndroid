package what.whatandroid.bookmarks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.bookmarks.Artist;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewArtistCallbacks;

/**
 * Displays a list of the user's artist bookmarks
 */
public class ArtistBookmarkAdapter extends ArrayAdapter<Artist> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private ViewArtistCallbacks viewArtist;
	private BookmarksChangedListener listener;

	public ArtistBookmarkAdapter(Context context, BookmarksChangedListener listener){
		super(context, R.layout.list_torrent_bookmark);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listener = listener;
		try {
			viewArtist = (ViewArtistCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtistCallbacks");
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
			holder.removeBookmark = (ImageButton)convertView.findViewById(R.id.remove_bookmark);
			//Hide unused views
			View year = convertView.findViewById(R.id.year);
			View tags = convertView.findViewById(R.id.tags);
			year.setVisibility(View.GONE);
			tags.setVisibility(View.GONE);
			convertView.setTag(holder);
		}
		final Artist artist = getItem(position);
		holder.title.setText(artist.getArtistName());
		holder.removeBookmark.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				new RemoveBookmarkTask().execute(artist);
				remove(artist);
				listener.bookmarksChanged();
			}
		});
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		viewArtist.viewArtist(getItem(position).getArtistId().intValue());
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public TextView title;
		public ImageButton removeBookmark;
	}

	/**
	 * Async task to unbookmark torrents
	 */
	private class RemoveBookmarkTask extends AsyncTask<Artist, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Artist... params){
			Artist artist = params[0];
			return api.torrents.artist.Artist.removeBookmark(artist.getArtistId().intValue());
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (!status){
				Toast.makeText(getContext(), "Could not remove bookmark", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
