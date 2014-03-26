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
	private TorrentSearch search;
	/**
	 * Callbacks to view the selected torrent group
	 */
	private ViewTorrentCallbacks callbacks;
	/**
	 * Loading indicator footer to hide once loading is done
	 */
	private View footer;
	/**
	 * Track if we're loading the next page of results
	 */
	private boolean loadingNext;

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

	/**
	 * Set the search currently being viewed
	 *
	 * @param search search to view
	 */
	public void viewSearch(TorrentSearch search){
		clear();
		//TODO: api level 11?
		addAll(search.getResponse().getResults());
		notifyDataSetChanged();
		this.search = search;
	}

	/**
	 * Clear the currently viewed search
	 */
	public void clearSearch(){
		clear();
		notifyDataSetChanged();
		search = null;
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
			holder.art = (ImageView)convertView.findViewById(R.id.album_art);
			holder.artist = (TextView)convertView.findViewById(R.id.artist_name);
			holder.title = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.album_year);
			holder.tags = (TextView)convertView.findViewById(R.id.album_tags);
			convertView.setTag(holder);
		}
		TorrentGroup group = getItem(position);
		if (group.getCover() != null){
			ImageLoader.getInstance().displayImage(group.getCover(), holder.art);
			holder.art.setVisibility(View.VISIBLE);
		}
		else {
			holder.art.setVisibility(View.GONE);
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
			callbacks.viewTorrentGroup(getItem(position - 1).getGroupId().intValue());
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//Load more if we're within 15 items of the end and there's a next page to load
		if (search != null && search.hasNextPage() && !loadingNext && firstVisibleItem + visibleItemCount + 10 >= totalItemCount){
			loadingNext = true;
			new LoadNextPage().execute();
		}
	}

	/**
	 * View holder for the torrent group information
	 */
	private static class ViewHolder {
		public ImageView art;
		public TextView artist, title, year, tags;
	}

	/**
	 * Load the next page of the current torrent search
	 */
	private class LoadNextPage extends AsyncTask<Void, Void, TorrentSearch> {
		@Override
		protected void onPreExecute(){
			footer.setVisibility(View.VISIBLE);
		}

		@Override
		protected TorrentSearch doInBackground(Void... params){
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
