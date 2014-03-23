package what.whatandroid.callbacks;

import api.announcements.Announcements;

/**
 * Callbacks for the announcements activity to update the announcements or blog posts
 * fragments shown announcements
 */
public interface AnnouncementsFragmentCallbacks {
	/**
	 * Set the displayed announcements
	 *
	 * @param announcements announcements to display
	 */
	public void setAnnouncements(Announcements announcements);

	/**
	 * Notify the view pager for the fragment that back was pressed
	 *
	 * @return true if the activity should go back, false if the view pager went back
	 */
	public boolean backPressed();
}
