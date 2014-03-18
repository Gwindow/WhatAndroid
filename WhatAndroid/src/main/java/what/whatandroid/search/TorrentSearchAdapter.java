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

	public void viewSearch(String terms, String tags){
		clear();
		notifyDataSetChanged();
		footer.setVisibility(View.VISIBLE);
		System.out.println("Searching for terms=" + terms + ", tags=" + tags);
		loadingNext = true;
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
		//Clicking the footer gives us an out of bounds click event
		if (position < getCount()){
			callbacks.viewTorrentGroup(getItem(position).getGroupId().intValue());
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//Load more if we're within 15 items of the end and there's a next page to load
		if (search != null && search.hasNextPage() && !loadingNext && firstVisibleItem + visibleItemCount + 15 >= totalItemCount){
			loadingNext = true;
			new LoadTorrentSearch().execute();
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
	 * Load the first page of torrent search results in the background, params should be { terms, tags }, or
	 * empty if a search has already been loaded and we want to load the next page
	 */
	private class LoadTorrentSearch extends AsyncTask<String, Void, TorrentSearch> {
		@Override
		protected TorrentSearch doInBackground(String... params){
			try {
				TorrentSearch s;
				//If we want to load the next page of the existing search
				if (params.length == 0 && search != null){
					s = search.nextPage();
				}
				else {
					s = TorrentSearch.search(params[0], params[1]);
				}
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
