package what.whatandroid.search;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.cli.Utils;
import api.search.requests.Request;
import api.soup.MySoup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewRequestCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

import java.util.Date;

/**
 * Adapter to display request search results
 */
public class RequestSearchAdapter extends ArrayAdapter<Request> implements AdapterView.OnItemClickListener {
	private final Context context;
	private final LayoutInflater inflater;
	/**
	 * Callbacks to view the selected request
	 */
	private ViewRequestCallbacks callbacks;

	/**
	 * Construct the empty adapter. A new search can be set to be displayed via viewSearch
	 */
	public RequestSearchAdapter(Context context, View footer){
		super(context, R.layout.list_request);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		try {
			callbacks = (ViewRequestCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewRequestCallbacks");
		}
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
			holder.artContainer = convertView.findViewById(R.id.art_container);
			holder.listener = new ImageLoadingListener(holder.spinner, holder.artContainer);
			holder.artistName = (TextView)convertView.findViewById(R.id.artist_name);
			holder.albumName = (TextView)convertView.findViewById(R.id.album_name);
			holder.year = (TextView)convertView.findViewById(R.id.year);
			holder.votes = (TextView)convertView.findViewById(R.id.votes);
			holder.bounty = (TextView)convertView.findViewById(R.id.bounty);
			holder.created = (TextView)convertView.findViewById(R.id.created);
			convertView.setTag(holder);
		}
		Request r = getItem(position);
		holder.albumName.setText(r.getTitle());
		holder.votes.setText(r.getVoteCount().toString());
		holder.bounty.setText(Utils.toHumanReadableSize(r.getBounty().longValue()));
		Date createDate = MySoup.parseDate(r.getTimeAdded());
		holder.created.setText(DateUtils.getRelativeTimeSpanString(createDate.getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

		if (r.getArtists().isEmpty() || r.getArtists().get(0).isEmpty()){
			holder.artistName.setVisibility(View.GONE);
		}
		else {
			holder.artistName.setVisibility(View.VISIBLE);
			if (r.getArtists().get(0).size() > 2){
				holder.artistName.setText("Various Artists");
			}
			else if (r.getArtists().get(0).size() == 2){
				holder.artistName.setText(r.getArtists().get(0).get(0).getName()
					+ " & " + r.getArtists().get(0).get(1).getName());
			}
			else {
				holder.artistName.setText(r.getArtists().get(0).get(0).getName());
			}
		}

		String imgUrl = r.getImage();
		if (SettingsActivity.imagesEnabled(context) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, holder.art, holder.listener);
		}
		else {
			holder.artContainer.setVisibility(View.GONE);
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

	private class ViewHolder {
		public ImageView art;
		public ProgressBar spinner;
		public View artContainer;
		public ImageLoadingListener listener;
		public TextView artistName, albumName, year, votes, bounty, created;
	}
}
