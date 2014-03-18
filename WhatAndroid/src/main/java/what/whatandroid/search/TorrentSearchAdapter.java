package what.whatandroid.search;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import api.search.torrents.TorrentGroup;
import api.search.torrents.TorrentSearch;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Adapter for viewing list of torrent search results
 */
public class TorrentSearchAdapter extends ArrayAdapter<TorrentGroup> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	/**
	 * The search being viewed
	 */
	TorrentSearch search;
	/**
	 * Callbacks to view the selected torrent group
	 */
	ViewTorrentCallbacks callbacks;

	/**
	 * Construct the empty adapter. A new search can be set to be viewed in the adapter by
	 * calling viewSearch
	 *
	 * @param context application context for the adapter
	 */
	public TorrentSearchAdapter(Context context){
		super(context, R.layout.list_artist_torrent);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			callbacks = (ViewTorrentCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	public void viewSearch(String terms, String tags){
		clear();
		notifyDataSetChanged();
		//TODO: Should show a loading spinner
		System.out.println("Searching for terms=" + terms + ", tags=" + tags);
		new LoadTorrentSearch().execute(terms, tags);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_artist_torrent, parent, false);
			holder = new ViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.album_art);
			holder.title = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			convertView.setTag(holder);
		}
		holder.torrentGroup = (TorrentGroup)getItem(position);
		ImageLoader.getInstance().displayImage(holder.torrentGroup.getCover(), holder.art);
		holder.title.setText(holder.torrentGroup.getArtist() + " - " + holder.torrentGroup.getGroupName());
		holder.year.setText(holder.torrentGroup.getReleaseType() + " [" + holder.torrentGroup.getGroupYear() + "]");
		//TODO: Better tag serialization?
		holder.tags.setText(holder.torrentGroup.getTags().toString());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		callbacks.viewTorrentGroup(getItem(position).getGroupId().intValue());
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public TorrentGroup torrentGroup;
		public ImageView art;
		public TextView title, year, tags;
	}

	/**
	 * Load the first page of torrent search results in the background, params should be { terms, tags }
	 */
	private class LoadTorrentSearch extends AsyncTask<String, Void, TorrentSearch> {

		@Override
		protected TorrentSearch doInBackground(String... params){
			try {
				TorrentSearch s = TorrentSearch.search(params[0], params[1]);
				if (s != null && s.getStatus()){
					return s;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(TorrentSearch torrentSearch){
			if (torrentSearch != null){
				search = torrentSearch;
				addAll(search.getResponse().getResults());
				notifyDataSetChanged();
			}
			//Else show an error
		}
	}
}
