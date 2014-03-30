package what.whatandroid.announcements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.widget.Toast;
import api.announcements.Announcements;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.AnnouncementsFragmentCallbacks;
import what.whatandroid.login.LoggedInActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;

/**
 * The announcements fragment shows announcements and blog posts and is the "main" activity, being
 * the first one shown after logging in
 */
public class AnnouncementsActivity extends LoggedInActivity {
	/**
	 * Intent parameters for showing Announcements or Blogs
	 */
	public final static String SHOW = "what.whatandroid.SHOW";
	public final static int ANNOUNCEMENTS = 0, BLOGS = 1;
	/**
	 * Callback to update the displayed fragments list of blog posts or announcements
	 */
	private AnnouncementsFragmentCallbacks callbacks;
	/**
	 * The announcements being displayed
	 */
	private Announcements announcements;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		int show = getIntent().getIntExtra(SHOW, ANNOUNCEMENTS);
		Fragment fragment;

		fragment = new AnnouncementsFragment();
		callbacks = (AnnouncementsFragmentCallbacks)fragment;
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.container, fragment).commit();
	}

	@Override
	public void onLoggedIn(){
		if (announcements == null){
			new LoadAnnouncements().execute();
		}
	}

	@Override
	public void onBackPressed(){
		//If the activity should go back, go back
		if (callbacks.backPressed()){
			super.onBackPressed();
		}
	}

	/**
	 * Select the either an adapter for viewing blog posts/announcements or transition
	 * to a new activity
	 *
	 * @param position position in the nav drawer of the item selected
	 */
	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements)) && announcements != null){
			//Instead swap fragment like Search activity
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog)) && announcements != null){
			//Instead swap fragment like search activity
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.torrents))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.artists))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.ARTIST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.requests))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.REQUEST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.users))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			startActivity(intent);
		}
	}

	/**
	 * Async task to load the announcements
	 */
	private class LoadAnnouncements extends AsyncTask<Void, Void, Announcements> {
		/**
		 * params[0] should be which announcements we want to show after loading is done,
		 * announcements or blogs
		 *
		 * @param params What to show after we're done loading
		 * @return the loaded announcements
		 */
		@Override
		protected Announcements doInBackground(Void... params){
			try {
				return Announcements.init();
			}
			catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminateVisibility(true);
			setProgressBarIndeterminate(true);
		}

		@Override
		protected void onPostExecute(Announcements announce){
			setProgressBarIndeterminateVisibility(false);
			setProgressBarIndeterminate(false);
			if (announce != null){
				announcements = announce;
				callbacks.setAnnouncements(announcements);
			}
			else {
				Toast.makeText(AnnouncementsActivity.this, "Loading announcements failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
