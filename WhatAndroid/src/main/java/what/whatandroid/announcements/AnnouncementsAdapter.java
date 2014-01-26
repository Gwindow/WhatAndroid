package what.whatandroid.announcements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.announcements.Announcement;
import what.whatandroid.R;

import java.util.List;

/**
 * List adapter for the Announcments fragment
 */
public class AnnouncementsAdapter extends ArrayAdapter<Announcement> implements View.OnClickListener {
	private final LayoutInflater inflater;
	private final int resource;
	private List<Announcement> announcements;
	/**
	 * The announcement activity interface so we can select an announcement to show
	 * a detail view of
	 */
	private AnnouncementManager manager;

	/**
	 * Construct the adapter and assign the list of announcements to view
	 * @param context application context for the adapter
	 * @param resource the view to inflate
	 * @param objects the objects to display
	 */
	public AnnouncementsAdapter(Context context, int resource, List<Announcement> objects) {
		super(context, resource, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		announcements = objects;
		try {
			manager = (AnnouncementManager)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement AnnouncementManager");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Recycle views if we can
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		//We need to setup a new view
		else {
			convertView = inflater.inflate(resource, parent, false);
			convertView.setOnClickListener(this);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.announcement_title);
			holder.date = (TextView)convertView.findViewById(R.id.announcement_date);
			holder.snippet = (TextView)convertView.findViewById(R.id.announcement_snippet);
			convertView.setTag(holder);
		}
		holder.announcement = announcements.get(position);
		holder.title.setText(holder.announcement.getTitle());
		//TODO: Prettier formatting for newstime maybe? Fuzzy time?
		holder.date.setText(holder.announcement.getNewsTime());
		holder.snippet.setText(holder.announcement.getBody());
		return convertView;
	}

	/**
	 * When an announcement is clicked we want to show a detail view of it that shows
	 * the full announcement text
	 */
	@Override
	public void onClick(View v) {
		ViewHolder holder = (ViewHolder)v.getTag();
		manager.showAnnouncement(holder.announcement);
	}

	/**
	 * View holder for Announcements for quicker look-ups of views
	 */
	private static class ViewHolder {
		public TextView title, date, snippet;
		public Announcement announcement;
	}
}
