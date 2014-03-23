package what.whatandroid.announcements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.announcements.Announcement;
import api.announcements.Announcements;
import what.whatandroid.R;
import what.whatandroid.callbacks.AnnouncementsFragmentCallbacks;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.callbacks.ViewAnnouncementCallbacks;

import java.util.List;

/**
 * The Announcements fragment displays a list of the site
 * announcements in a list adapter. Selecting an announcement will open
 * it in a new fragment to the right.
 */
public class AnnouncementsFragment extends Fragment implements AnnouncementsFragmentCallbacks, ViewAnnouncementCallbacks {
	/**
	 * The announcements we're showing
	 */
	private List<Announcement> announcements;
	/**
	 * The view pager containing the announcements list and the detail fragment
	 */
	private ViewPager viewPager;
	/**
	 * The list and detail view fragments so we can change what's being shown
	 */
	private AnnouncementsListFragment listFragment;
	private AnnouncementDetailFragment detailFragment;
	private SetTitleCallback setTitleCallback;

	/**
	 * Use this factory method to create an AnnouncementsFragment displaying the list of announcements
	 *
	 * @param announcements the announcements to display
	 * @return an AnnouncementsFragment displaying the announcements
	 */
	public static AnnouncementsFragment newInstance(List<Announcement> announcements){
		AnnouncementsFragment fragment = new AnnouncementsFragment();
		fragment.announcements = announcements;
		return fragment;
	}

	public AnnouncementsFragment(){
		//Required blank ctor
	}

	/**
	 * Set the announcements being viewed
	 * @param announcements the announcements to display
	 */
	@Override
	public void setAnnouncements(Announcements announcements){
		this.announcements = announcements.getResponse().getAnnouncements();
		listFragment.setAnnouncements(this.announcements);
		detailFragment.setAnnouncement(this.announcements.get(0));
	}

	@Override
	public boolean backPressed(){
		if (viewPager.getCurrentItem() == 0){
			return true;
		}
		viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
		return false;
	}

	@Override
	public void viewAnnouncement(Announcement announcement){
		detailFragment.setAnnouncement(announcement);
		viewPager.setCurrentItem(1);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitleCallback = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement set title callbacks");
		}
		setTitleCallback.setTitle(getString(R.string.announcements));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_announcements, container, false);
		listFragment = AnnouncementsListFragment.newInstance(announcements, this);
		if (announcements != null){
			detailFragment = AnnouncementDetailFragment.newInstance(announcements.get(0));
		}
		else {
			detailFragment = AnnouncementDetailFragment.newInstance(null);
		}
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		viewPager.setAdapter(new AnnouncementsViewPagerAdapter(getChildFragmentManager()));
		return view;
	}

	/**
	 * ViewPagerAdapter for the announcements/blogs list and detail fragments
	 */
	private class AnnouncementsViewPagerAdapter extends FragmentStatePagerAdapter {
		public AnnouncementsViewPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public Fragment getItem(int position){
			return position == 0 ? listFragment : detailFragment;
		}

		@Override
		public int getCount(){
			return 2;
		}
	}
}
