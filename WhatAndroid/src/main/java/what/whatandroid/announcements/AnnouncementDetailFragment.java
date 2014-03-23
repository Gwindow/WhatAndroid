package what.whatandroid.announcements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import api.announcements.Announcement;
import what.whatandroid.R;

/**
 * Displays an announcement
 */
public class AnnouncementDetailFragment extends Fragment {
	private Announcement announcement;
	/**
	 * View displaying the announcement information
	 */
	TextView title, date, body;

    /**
     * Use this factory method to create a new instance of the fragment to display the announcement
     * @param announcement the announcement to display
     * @return An AnnouncementFragment displaying the desired announcement
	 */
	public static AnnouncementDetailFragment newInstance(Announcement announcement){
		AnnouncementDetailFragment fragment = new AnnouncementDetailFragment();
		fragment.announcement = announcement;
        return fragment;
	}

	public AnnouncementDetailFragment() {
		// Required empty public constructor
	}

	public void setAnnouncement(Announcement announcement){
		this.announcement = announcement;
		updateAnnouncement();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_announcement_detail, container, false);
		title = (TextView)view.findViewById(R.id.announcement_title);
		date = (TextView)view.findViewById(R.id.announcement_date);
		body = (TextView)view.findViewById(R.id.announcement_body);
		if (announcement != null){
			updateAnnouncement();
		}
		return view;
	}

	private void updateAnnouncement(){
		title.setText(announcement.getTitle());
		date.setText(announcement.getNewsTime());
		body.setText(announcement.getBody());
	}
}
