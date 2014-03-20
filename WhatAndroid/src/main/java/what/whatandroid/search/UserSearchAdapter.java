package what.whatandroid.search;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.search.user.User;
import api.search.user.UserSearch;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewUserCallbacks;

/**
 * Adapter for viewing a list of user search results
 */
public class UserSearchAdapter extends ArrayAdapter<User>
	implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

	private final LayoutInflater inflater;
	/**
	 * The search being viewed
	 */
	private UserSearch search;
	/**
	 * Callbacks to view the selected user
	 */
	private ViewUserCallbacks callbacks;
	/**
	 * Loading indicater footer to hide once loading is done
	 */
	private View footer;
	/**
	 * Track if we're loading the next page of results
	 */
	private boolean loadingNext;

	/**
	 * Construct the empty adapter. a new search can be set to be viewed by calling viewSearch
	 *
	 * @param context context to create the adapter in. Must implement ViewUserCallbacks
	 * @param footer  loading indicator footer
	 */
	public UserSearchAdapter(Context context, View footer){
		super(context, R.layout.list_user_search);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.footer = footer;
		loadingNext = false;
		try {
			callbacks = (ViewUserCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewUserCallbacks");
		}
	}

	/**
	 * Set the search the adapter will be viewing
	 *
	 * @param search search to view
	 */
	public void viewSearch(UserSearch search){
		clear();
		//TODO API level 11?
		addAll(search.getResponse().getResults());
		notifyDataSetChanged();
		this.search = search;
	}

	/**
	 * Clear out the currently viewed search from the adapter
	 */
	public void clearSearch(){
		clear();
		search = null;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_user_search, parent, false);
			holder = new ViewHolder();
			holder.userName = (TextView)convertView.findViewById(R.id.user_name);
			holder.userClass = (TextView)convertView.findViewById(R.id.user_class);
			convertView.setTag(holder);
		}
		User user = getItem(position);
		holder.userName.setText(user.getUsername());
		holder.userClass.setText(user.getUserClass());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Clicking the footer gives us an out of bounds click event, so subtract one to account for this
		if (position - 1 < getCount()){
			callbacks.viewUser(getItem(position - 1).getUserId().intValue());
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
	 * View holder for various information about the users
	 */
	private static class ViewHolder {
		public TextView userName, userClass;
	}

	private class LoadNextPage extends AsyncTask<Void, Void, UserSearch> {
		@Override
		protected UserSearch doInBackground(Void... params){
			try {
				UserSearch s = search.nextPage();
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
		protected void onPreExecute(){
			footer.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(UserSearch userSearch){
			loadingNext = false;
			if (userSearch != null){
				search = userSearch;
				addAll(search.getResponse().getResults());
				notifyDataSetChanged();
			}
			//Else show error toast
			if (userSearch == null || !search.hasNextPage()){
				footer.setVisibility(View.GONE);
			}
		}
	}
}
