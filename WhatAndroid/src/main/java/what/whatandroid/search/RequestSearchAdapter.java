package what.whatandroid.search;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.cli.Utils;
import api.search.requests.Request;
import api.search.requests.RequestsSearch;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

/**
 * Adapter to display request search results
 */
public class RequestSearchAdapter extends ArrayAdapter<Request>
	implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

	private final Context context;
	private final LayoutInflater inflater;
	/**
	 * The search being viewed
	 */
	private RequestsSearch search;
	/**
	 * Callbacks to view the selected request
	 */
	private ViewRequestCallbacks callbacks;
	/**
	 * Loading indicator footer to hide once loading is done
	 */
	private View footer;
	/**
	 * Track if we're loading the next page of results
	 */
	private boolean loadingNext;

	/**
	 * Construct the empty adapter. A new search can be set to be displayed via viewSearch
	 */
	public RequestSearchAdapter(Context context, View footer){
		super(context, R.layout.list_request);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.footer = footer;
		loadingNext = false;
		try {
			callbacks = (ViewRequestCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewRequestCallbacks");
		}
	}

	public void viewSearch(RequestsSearch search){
		clear();
		addAll(search.getResponse().getResults());
		notifyDataSetChanged();
		this.search = search;
	}

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
			convertView = inflater.inflate(R.layout.list_request_search, parent, false);
			holder = new ViewHolder();
			holder.art = (ImageView)convertView.findViewById(R.id.art);
			holder.spinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.listener = new ImageLoadingListener(holder.spinner);
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.year = (TextView)convertView.findViewById(R.id.year);
			holder.votes = (TextView)convertView.findViewById(R.id.votes);
			holder.bounty = (TextView)convertView.findViewById(R.id.bounty);
			holder.created = (TextView)convertView.findViewById(R.id.created);
			convertView.setTag(holder);
		}
		Request r = getItem(position);
		holder.title.setText(r.getTitle());
		holder.votes.setText(r.getVoteCount().toString());
		holder.bounty.setText(Utils.toHumanReadableSize(r.getBounty().longValue()));
		holder.created.setText(r.getTimeAdded());

		String imgUrl = r.getImage();
		if (SettingsActivity.imagesEnabled(context) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, holder.art, holder.listener);
		}
		else {
			holder.art.setVisibility(View.GONE);
			holder.spinner.setVisibility(View.GONE);
		}
		if (r.getYear().intValue() != 0){
			holder.year.setText(r.getYear().toString());
		}
		else {
			holder.year.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Clicking the footer gives us an out of bounds event so account for this
		if (position - 1 < getCount()){
			callbacks.viewRequest(getItem(position - 1).getRequestId().intValue());
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//Load more if we're within 15 items of the end of the page & there's more to load
		if (search != null && search.hasNextPage() && !loadingNext && firstVisibleItem + visibleItemCount + 10 >= totalItemCount){
			loadingNext = true;
			new LoadNextPage().execute();
		}
	}

	private class ViewHolder {
		public ImageView art;
		public ProgressBar spinner;
		public ImageLoadingListener listener;
		public TextView title, year, votes, bounty, created;
	}

	/**
	 * Load the next page of the current search
	 */
	private class LoadNextPage extends AsyncTask<Void, Void, RequestsSearch> {
		@Override
		protected void onPreExecute(){
			footer.setVisibility(View.VISIBLE);
		}

		@Override
		protected RequestsSearch doInBackground(Void... params){
			try {
				RequestsSearch s = search.nextPage();
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
		protected void onPostExecute(RequestsSearch requestsSearch){
			loadingNext = false;
			if (requestsSearch != null){
				search = requestsSearch;
				addAll(search.getResponse().getResults());
				notifyDataSetChanged();
			}
			//Else show a toast error?
			if (requestsSearch == null || !search.hasNextPage()){
				footer.setVisibility(View.GONE);
			}
		}
	}
}
