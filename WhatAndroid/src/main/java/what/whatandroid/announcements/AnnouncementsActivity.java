package what.whatandroid.announcements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import api.announcements.Announcement;
import api.announcements.Announcements;
import api.announcements.BlogPost;
import api.soup.MySoup;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.profile.ProfileActivity;

/**
 * The announcements fragment shows announcements and blog posts and is the "main" activity, being
 * the first one shown after logging in. The navbar routes user to other activities in the app
 * TODO: Need a way of handling the html in the blog and announcement bodies
 * TODO: Side note, blog posts API doesn't seem to work on my gazelle install, maybe just b/c it's older?
 * they do seem to work well against the full site.
 */
public class AnnouncementsActivity extends ActionBarActivity
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, AnnouncementManager {
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navDrawer;
	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence title;
	/**
	 * Our pager adapter, view pager and the number of fragments we're showing
	 */
	private PagerAdapter pagerAdapter;
	private ViewPager viewPager;
	/**
	 * The announcements being shown in the view (announcements and blog posts)
	 */
	private Announcements announcements;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_announcements);

		navDrawer = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		title = getString(R.string.announcements);
		getSupportActionBar().setTitle(title);

		//Set up the drawer.
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		viewPager = (ViewPager) findViewById(R.id.view_pager);

		//TODO: Show an indeterminate progress bar somewhere
		new LoadAnnouncements().execute();
	}

	/**
	 * Select the either an adapter for viewing blog posts/announcements or transition
	 * to a new activity
	 *
	 * @param position position in the nav drawer of the item selected
	 */
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (navDrawer == null) {
			return;
		}

		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements)) && announcements != null){
			pagerAdapter = new AnnouncementsPagerAdapter(getSupportFragmentManager());
			viewPager.setAdapter(pagerAdapter);
			title = getString(R.string.announcements);
			getSupportActionBar().setTitle(title);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog)) && announcements != null){
			pagerAdapter = new BlogPostsPagerAdapter(getSupportFragmentManager());
			viewPager.setAdapter(pagerAdapter);
			title = getString(R.string.blog);
			getSupportActionBar().setTitle(title);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))) {
			//Launch profile view activity
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		}
	}

	/**
	 * Set the announcement to be shown and show it
	 * Ignored if not currently in the announcements view
	 *
	 * @param announcement the announcement to show in the fragment
	 */
	@Override
	public void showAnnouncement(Announcement announcement) {
		if (pagerAdapter instanceof AnnouncementsPagerAdapter) {
			((AnnouncementsPagerAdapter) pagerAdapter).showAnnouncement(announcement);
			viewPager.setCurrentItem(1);
		}
	}

	/**
	 * Set the blog post to be shown and show it
	 * Ignored if not currently in the blog posts view
	 *
	 * @param post the blog post to show in the fragment
	 */
	@Override
	public void showBlogPost(BlogPost post) {
		if (pagerAdapter instanceof BlogPostsPagerAdapter) {
			((BlogPostsPagerAdapter) pagerAdapter).showPost(post);
			viewPager.setCurrentItem(1);
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!navDrawer.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.what_android, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		//If we're at the last view go back in the activity stack
		if (viewPager.getCurrentItem() == 0) {
			super.onBackPressed();
		}
		else {
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
		}
	}

	/**
	 * A slide pager adapter for showing the list of announcements and
	 * a detail fragment of the selected announcement
	 */
	private class AnnouncementsPagerAdapter extends FragmentStatePagerAdapter {
		/**
		 * The announcement fragment for the announcement being shown in detail
		 */
		private AnnouncementFragment detail;
		private int numPages;
		private final FragmentManager fragmentManager;

		public AnnouncementsPagerAdapter(FragmentManager fm) {
			super(fm);
			fragmentManager = fm;
			numPages = 1;
		}

		/**
		 * Set the announcement to view in detail
		 *
		 * @param announcement the announcement to view in detail
		 */
		public void showAnnouncement(Announcement announcement) {
			if (detail != null && !detail.getAnnouncement().getTitle().equalsIgnoreCase(announcement.getTitle())) {
				fragmentManager.beginTransaction().remove(detail).commit();
			}
			detail = AnnouncementFragment.newInstance(announcement);
			numPages = 2;
			notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int i) {
			if (i == 0) {
				return AnnouncementsFragment.newInstance(announcements.getResponse().getAnnouncements());
			}
			return detail;
		}

		@Override
		public int getItemPosition(Object object) {
			//If the detail fragment we want to show has a different title than the one being shown tell it to hide
			//the old detail fragment
			if (object instanceof AnnouncementFragment && !((AnnouncementFragment) object).getAnnouncement().getTitle()
				.equalsIgnoreCase(detail.getAnnouncement().getTitle())) {
				return POSITION_NONE;
			}
			return POSITION_UNCHANGED;
		}

		@Override
		public int getCount() {
			return numPages;
		}
	}

	/**
	 * A slide pager adapter for showing the list of blog posts and a detail
	 * fragment of the selected post
	 */
	private class BlogPostsPagerAdapter extends FragmentStatePagerAdapter {
		/**
		 * The announcement fragment for the announcement being shown in detail
		 */
		private BlogPostFragment detail;
		private int numPages;
		private final FragmentManager fragmentManager;

		public BlogPostsPagerAdapter(FragmentManager fm) {
			super(fm);
			fragmentManager = fm;
			numPages = 1;
		}

		/**
		 * Set the blog post to view in detail
		 *
		 * @param post the announcement to view in detail
		 */
		public void showPost(BlogPost post) {
			if (detail != null && !detail.getPost().getTitle().equalsIgnoreCase(post.getTitle())) {
				fragmentManager.beginTransaction().remove(detail).commit();
			}
			detail = BlogPostFragment.newInstance(post);
			numPages = 2;
			notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int i) {
			if (i == 0) {
				return BlogPostsFragment.newInstance(announcements.getResponse().getBlogPosts());
			}
			return detail;
		}

		@Override
		public int getItemPosition(Object object) {
			//If the detail fragment we want to show has a different title than the one being shown tell it to hide
			//the old detail fragment
			if (object instanceof BlogPostFragment && !((BlogPostFragment) object).getPost().getTitle()
				.equalsIgnoreCase(detail.getPost().getTitle())) {
				return POSITION_NONE;
			}
			return POSITION_UNCHANGED;
		}

		@Override
		public int getCount() {
			return numPages;
		}
	}

	/**
	 * Async task to load the announcements
	 */
	private class LoadAnnouncements extends AsyncTask<Void, Void, Announcements> {
		@Override
		protected Announcements doInBackground(Void... params) {
			try {
				return Announcements.init();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Announcements announce) {
			if (announce != null) {
				announcements = announce;
				pagerAdapter = new AnnouncementsPagerAdapter(getSupportFragmentManager());
				viewPager.setAdapter(pagerAdapter);
			}
			else {
				Toast.makeText(AnnouncementsActivity.this, "Loading announcements failed", Toast.LENGTH_LONG).show();
			}
		}
	}
}
