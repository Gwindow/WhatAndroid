package what.whatandroid.callbacks;

import api.announcements.Announcement;

/**
 * Callbacks for the announcement fragment to set what's being show
 * in the detail view
 */
public interface ViewAnnouncementCallbacks {
	/**
	 * Set the announcement being shown in detail
	 *
	 * @param announcement announcement to show in detail
	 */
	public void viewAnnouncement(Announcement announcement);

}
