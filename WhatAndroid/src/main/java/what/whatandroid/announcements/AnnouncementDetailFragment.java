package what.whatandroid.announcements;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import api.announcements.Announcement;
import what.whatandroid.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Displays an announcement
 */
public class AnnouncementDetailFragment extends ListFragment {
	private Context context;
	private Announcement announcement;
	private AnnouncementDetailAdapter adapter;
	/**
	 * View displaying the announcement information
	 */
	View header;
	TextView title, date;

	/**
	 * Use this factory method to create a new instance of the fragment to display the announcement
	 *
	 * @param announcement the announcement to display
	 * @return An AnnouncementFragment displaying the desired announcement
	 */
	public static AnnouncementDetailFragment newInstance(Announcement announcement){
		AnnouncementDetailFragment fragment = new AnnouncementDetailFragment();
		fragment.announcement = announcement;
		return fragment;
	}

	public AnnouncementDetailFragment(){
		// Required empty public constructor
	}

	public void setAnnouncement(Announcement announcement){
		this.announcement = announcement;
		updateAnnouncement();
		if (adapter == null){
			//Will splitting the announcement be too costly? We could do it in the loading task if we want
			adapter = new AnnouncementDetailAdapter(context,
				new ArrayList<String>(Arrays.asList(announcement.getBody().split("<br />+"))));
			setListAdapter(adapter);
		}
		adapter.clear();
		adapter.addAll(announcement.getBody().split("<br />+"));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		getListView().addHeaderView(header);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = super.onCreateView(inflater, container, savedInstanceState);
		header = inflater.inflate(R.layout.header_announcement, null);
		title = (TextView)header.findViewById(R.id.announcement_title);
		date = (TextView)header.findViewById(R.id.announcement_date);
		return view;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		context = activity;
		if (adapter == null && announcement != null){
			//Will splitting the announcement be too costly? We could do it in the loading task if we want
			adapter = new AnnouncementDetailAdapter(context,
				new ArrayList<String>(Arrays.asList(announcement.getBody().split("<br />+"))));
			setListAdapter(adapter);
		}
	}

	private void updateAnnouncement(){
		title.setText(announcement.getTitle());
		date.setText(announcement.getNewsTime());
	}
}
