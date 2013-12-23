package what.whatandroid.announcements;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import api.announcements.Announcements;
import what.whatandroid.R;

/**
 * The Announcements fragment displays a list of the site
 * announcements in a list adapter. Selecting an announcement will open
 * it in a new fragment to the right.
 */
public class AnnouncementsFragment extends ListFragment {
	private Announcements announcements;
	private AnnouncementsAdapter adapter;

	/**
	 * Use this factory method to create an AnnouncementsFragment displaying the list of announcements
	 * @param announcements the announcements to display
	 * @return an AnnouncementsFragment displaying the announcements
	 */
	public static AnnouncementsFragment newInstance(Announcements announcements){
		AnnouncementsFragment fragment = new AnnouncementsFragment();
		fragment.announcements = announcements;
		return fragment;
	}

	public AnnouncementsFragment() {
		//Required blank ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		adapter = new AnnouncementsAdapter(getActivity(), R.layout.list_announcement,
			announcements.getResponse().getAnnouncements());
		setListAdapter(adapter);
	}
}
