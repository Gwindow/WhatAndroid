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
public class AnnouncementFragment extends Fragment {
	private Announcement announcement;

    /**
     * Use this factory method to create a new instance of the fragment to display
	 * the passed announcement
     * @param announcement the announcement to display
     * @return An AnnouncementFragment displaying the desired announcement
     */
    public static AnnouncementFragment newInstance(Announcement announcement) {
        AnnouncementFragment fragment = new AnnouncementFragment();
		fragment.announcement = announcement;
        return fragment;
    }

    public AnnouncementFragment() {
        // Required empty public constructor
    }

	/**
	 * Get the announcement being displayed
	 * @return the announcement being displayed
	 */
	public Announcement getAnnouncement(){
		return announcement;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);
		TextView title = (TextView)view.findViewById(R.id.announcement_title);
		TextView date = (TextView)view.findViewById(R.id.announcement_date);
		TextView body = (TextView)view.findViewById(R.id.announcement_body);
		title.setText(announcement.getTitle());
		date.setText(announcement.getNewsTime());
		body.setText(announcement.getBody());
		return view;
    }


}
