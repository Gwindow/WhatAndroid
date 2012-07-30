package what.user;

import what.fragments.ArtFragment;
import what.fragments.DescriptionFragment;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import what.inbox.NewMessageActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import api.soup.MySoup;
import api.user.User;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jun 3, 2012 10:07:49 AM
 */
public class UserActivity extends MyActivity2 {
	private static final int FRIENDS_ITEM_ID = 0;

	private static final String AVATAR_TAB = "Avatar";
	private static final String PROFILE_TAB = "Profile";
	private static final String STATS_TAB = "Stats";

	private static final String[] TABS = new String[] { AVATAR_TAB, PROFILE_TAB, STATS_TAB };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private User user;
	private int userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.USER);
		super.requestIndeterminateProgress();
		super.onCreate(savedInstanceState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		try {
			userId = bundle.getInt(BundleKeys.USER_ID);
		} catch (Exception e) {
		}

		new Load().execute();

	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		invalidateOptionsMenu();
		setContentView(R.layout.user_tabs);

		setActionBarTitle(user.getProfile().getUsername());

		adapter = new UserAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.user_menu, menu);
		if (user != null) {
			if (MySoup.getUsername().equalsIgnoreCase(user.getProfile().getUsername())) {
				menu.getItem(0).setEnabled(false);
				menu.getItem(0).setVisible(false);
			}
		}
		/*
		 * if (user != null && !user.getProfile().IsFriend()) { String title = "Add Friend"; menu.addSubMenu(Menu.NONE,
		 * FRIENDS_ITEM_ID, Menu.NONE, title); }
		 */
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.message_item:
				// close options menu for the fade effect
				closeOptionsMenu();
				sendMessage();
				break;
			case FRIENDS_ITEM_ID:
				addFriend();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendMessage() {
		Intent intent = new Intent(this, NewMessageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, userId);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void addFriend() {
		user.addToFriends();
		Toast.makeText(this, "Added to Friends list", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPause() {
		try {
			ArtFragment.recyle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		public Load() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			user = User.userFromId(userId);
			return user.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(UserActivity.this, UserActivity.class);
		}
	}

	private class UserAdapter extends FragmentPagerAdapter implements TitleProvider {
		public UserAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(AVATAR_TAB)) {
				fragment = new ArtFragment(user.getProfile().getAvatar(), R.drawable.dne);
			}
			if (tag.equals(PROFILE_TAB)) {
				fragment = new DescriptionFragment(user.getProfile().getProfileText());
			}
			if (tag.equals(STATS_TAB)) {
				fragment = new StatsFragment(user.getProfile());
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return TABS.length;
		}

		@Override
		public String getTitle(int position) {
			return TABS[position % TABS.length];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

}
