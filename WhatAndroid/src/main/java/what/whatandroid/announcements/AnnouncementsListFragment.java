package what.whatandroid.announcements;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import api.announcements.Announcement;
import what.whatandroid.callbacks.ViewAnnouncementCallbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * BlogPostsFragment displays a list of the blog posts and snippets of the body text
 * re-uses the layouts from the Announcements
 */
public class AnnouncementsListFragment extends ListFragment {
	private AnnouncementsAdapter adapter;
	private List<Announcement> announcements;
	private ViewAnnouncementCallbacks callbacks;

	/**
	 * Use this factory method to create a new instance of the fragment displaying the list of announcements
	 *
	 * @param announcements the announcements to display
	 * @param callbacks     callbacks to use to set the detail announcement being shown
	 */
	public static AnnouncementsListFragment newInstance(List<Announcement> announcements,
														ViewAnnouncementCallbacks callbacks){
		AnnouncementsListFragment fragment = new AnnouncementsListFragment();
		fragment.announcements = announcements == null ? new ArrayList<Announcement>() : announcements;
		fragment.callbacks = callbacks;
		return fragment;
	}

	public AnnouncementsListFragment(){
		// Required empty public constructor
	}

	public void setAnnouncements(List<Announcement> announcements){
		this.announcements = announcements;
		adapter.clear();
		adapter.addAll(announcements);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		adapter = new AnnouncementsAdapter(getActivity(), announcements, callbacks);
		setListAdapter(adapter);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(adapter);
	}
}
