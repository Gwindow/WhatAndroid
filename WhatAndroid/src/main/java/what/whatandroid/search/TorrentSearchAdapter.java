package what.whatandroid.search;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.search.torrents.TorrentGroup;
import api.search.torrents.TorrentSearch;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Adapter for viewing list of torrent search results
 */
public class TorrentSearchAdapter extends ArrayAdapter<TorrentGroup>
	implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

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
	 * Loading indicator footer to hide once loading is done
	 */
	View footer;
	/**
	 * Track if we're loading the next page of results
	 */
	boolean loadingNext;

	/**
	 * Construct the empty adapter. A new search can be set to be viewed in the adapter by
	 * calling viewSearch
	 * @param context application context for the adapter
	 * @param footer the footer loading indicator to hide once loading is complete
	 */
	public TorrentSearchAdapter(Context context, View footer){
		super(context, R.layout.list_artist_torrent);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.footer = footer;
		loadingNext = false;
		try {
			callbacks = (ViewTorrentCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	public void viewSearch(TorrentSearch search){
		clear();
		//TODO: api level 11?
		addAll(search.getResponse().getResults());
		notifyDataSetChanged();
		this.search = search;
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
		holder.torrentGroup = getItem(position);
		ImageLoader.getInstance().displayImage(holder.torrentGroup.getCover(), holder.art);
		if (holder.torrentGroup.getArtist() != null){
			holder.title.setText(holder.torrentGroup.getArtist() + " - " + holder.torrentGroup.getGroupName());
		}
		else {
			holder.title.setText(holder.torrentGroup.getGroupName());
		}
		if (holder.torrentGroup.getReleaseType() != null && holder.torrentGroup.getGroupYear() != null){
			holder.year.setText(holder.torrentGroup.getReleaseType() + " [" + holder.torrentGroup.getGroupYear() + "]");
		}
		else {
			holder.year.setVisibility(View.GONE);
		}
		holder.tags.setText(holder.torrentGroup.getTags().toString());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Clicking the footer gives us an out of bounds click event, we also must subtract 1 from the
		//position to account for the added footer
		if (position - 1 < getCount()){
			callbacks.viewTorrentGroup(getItem(position - 1).getGroupId().intValue());
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//Load more if we're within 15 items of the end and there's a next page to load
		if (search != null && search.hasNextPage() && !loadingNext && firstVisibleItem + visibleItemCount + 10 >= totalItemCount){
			loadingNext = true;
			new LoadNextPage().execute();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){

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
	 * Load the next page of the current torrent search
	 */
	private class LoadNextPage extends AsyncTask<String, Void, TorrentSearch> {
		@Override
		protected void onPreExecute(){
			footer.setVisibility(View.VISIBLE);
		}

		@Override
		protected TorrentSearch doInBackground(String... params){
			try {
				TorrentSearch s = search.nextPage();
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
			loadingNext = false;
			if (torrentSearch != null){
				search = torrentSearch;
				addAll(search.getResponse().getResults());
				notifyDataSetChanged();
			}
			//Else show a toast error
			if (torrentSearch == null || !search.hasNextPage()){
				footer.setVisibility(View.GONE);
			}
		}
	}
}
