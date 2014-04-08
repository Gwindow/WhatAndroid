package what.whatandroid.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.search.user.User;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewUserCallbacks;

/**
 * Adapter for viewing a list of user search results
 */
public class UserSearchAdapter extends ArrayAdapter<User> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	/**
	 * Callbacks to view the selected user
	 */
	private ViewUserCallbacks callbacks;

	/**
	 * Construct the empty adapter. a new search can be set to be viewed by calling viewSearch
	 *
	 * @param context context to create the adapter in. Must implement ViewUserCallbacks
	 * @param footer  loading indicator footer
	 */
	public UserSearchAdapter(Context context, View footer){
		super(context, R.layout.list_user_search);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			callbacks = (ViewUserCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewUserCallbacks");
		}
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
			holder.userName = (TextView)convertView.findViewById(R.id.username);
			holder.userClass = (TextView)convertView.findViewById(R.id.user_class);
			holder.donor = convertView.findViewById(R.id.donor);
			holder.warned = convertView.findViewById(R.id.warned);
			holder.banned = convertView.findViewById(R.id.banned);
			convertView.setTag(holder);
		}
		User user = getItem(position);
		holder.userName.setText(user.getUsername());
		holder.userClass.setText(user.getUserClass());
		if (user.isDonor()){
			holder.donor.setVisibility(View.VISIBLE);
		}
		else {
			holder.donor.setVisibility(View.GONE);
		}
		if (user.isWarned()){
			holder.warned.setVisibility(View.VISIBLE);
		}
		else {
			holder.warned.setVisibility(View.GONE);
		}
		if (!user.isEnabled()){
			holder.banned.setVisibility(View.VISIBLE);
		}
		else {
			holder.banned.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Clicking the footer gives us an out of bounds click event, so subtract one to account for this
		if (position - 1 < getCount()){
			callbacks.viewUser(getItem(position - 1).getUserId().intValue());
		}
	}

	/**
	 * View holder for various information about the users
	 */
	private static class ViewHolder {
		public TextView userName, userClass;
		public View donor, warned, banned;
	}
}
