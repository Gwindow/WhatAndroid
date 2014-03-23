package what.whatandroid.announcements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.announcements.Announcement;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewAnnouncementCallbacks;

import java.util.List;

/**
 * List adapter for the Announcments fragment
 */
public class AnnouncementsAdapter extends ArrayAdapter<Announcement> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	/**
	 * The announcement activity interface so we can select an announcement to show
	 * a detail view of
	 */
	ViewAnnouncementCallbacks callbacks;

	/**
	 * Construct the adapter and assign the list of announcements to view
	 *
	 * @param context application context for the adapter
	 * @param objects the objects to display
	 */
	public AnnouncementsAdapter(Context context, List<Announcement> objects, ViewAnnouncementCallbacks callbacks){
		super(context, R.layout.list_announcement, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callbacks = callbacks;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		//We need to setup a new view
		else {
			convertView = inflater.inflate(R.layout.list_announcement, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.announcement_title);
			holder.date = (TextView)convertView.findViewById(R.id.announcement_date);
			holder.snippet = (TextView)convertView.findViewById(R.id.announcement_snippet);
			convertView.setTag(holder);
		}
		Announcement a = getItem(position);
		holder.title.setText(a.getTitle());
		//TODO: Prettier formatting for newstime maybe? Fuzzy time?
		holder.date.setText(a.getNewsTime());
		//holder.snippet.setText(a.getBody());
		holder.snippet.setText("Empty");
		return convertView;
	}

	/**
	 * When an announcement is clicked we want to show a detail view of it that shows
	 * the full announcement text
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		callbacks.viewAnnouncement(getItem(position));
	}

	/**
	 * View holder for Announcements for quicker look-ups of views
	 */
	private static class ViewHolder {
		public TextView title, date, snippet;
	}
}
