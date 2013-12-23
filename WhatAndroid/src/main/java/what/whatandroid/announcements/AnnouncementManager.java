package what.whatandroid.announcements;

import api.announcements.Announcement;
import api.announcements.BlogPost;

/**
 * Interface for the Announcements/Blog fragments to communicate back to
 * the AnnouncementActivity so they can instruct it to show a new fragment or such
 */
public interface AnnouncementManager {
	/**
	 * Instruct the manager to show an AnnouncementFragment for the announcement
	 * @param announcement the announcement to show in the fragment
	 */
	public void showAnnouncement(Announcement announcement);

	/**
	 * Instruct the manager to show a BlogPostFragment for the blog post
	 * @param blogPost the blog post to show in the fragment
	 */
	public void showBlogPost(BlogPost blogPost);
}
