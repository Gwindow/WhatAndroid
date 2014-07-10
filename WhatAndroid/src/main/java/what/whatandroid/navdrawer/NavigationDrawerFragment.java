package what.whatandroid.navdrawer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.settings.SettingsActivity;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks drawerCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle drawerToggle;

	private DrawerLayout drawerLayout;
	private ListView listView;
	private View fragmentContainerview;
	private NavDrawerAdapter adapter;

	private int selectedPos = 0;
	private boolean fromSavedState;
	private boolean userLearnedDrawer;

	public NavigationDrawerFragment(){
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null){
			selectedPos = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			fromSavedState = true;
		}
		selectItem(selectedPos);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		listView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				selectItem(position);
			}
		});
		String navs[] = {
			//getString(R.string.announcements),
			//getString(R.string.blog),
			getString(R.string.profile),
			getString(R.string.bookmarks),
			//getString(R.string.inbox),
			getString(R.string.notifications),
			getString(R.string.subscriptions),
			getString(R.string.forums),
			getString(R.string.torrents),
			getString(R.string.artists),
			getString(R.string.requests),
			getString(R.string.users),
			getString(R.string.barcode_lookup)
		};
		ArrayList<String> navElems = new ArrayList<String>(Arrays.asList(navs));
		adapter = new NavDrawerAdapter(getActivity(), android.R.layout.simple_list_item_1,
			android.R.id.text1, navElems);

		listView.setAdapter(adapter);
		listView.setItemChecked(selectedPos, true);
		return listView;
	}

	@Override
	public void onResume(){
		super.onResume();
		//Also update the notifications information we're showing if we're logged in
		if (MySoup.isLoggedIn()){
			updateNotifications(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		}
	}

	/**
	 * Update the user's various notification nav items to reflect the status of any
	 * new notifications. This updates the torrent notifications, subscriptions and
	 * inbox nav items to alert the user if they have new notifications in the
	 * corresponding sections
	 *
	 * @param preferences Preferences to read the saved notifications state from
	 */
	public void updateNotifications(SharedPreferences preferences){
		updateTorrentNotifications(preferences);
		updateSubscriptions(preferences);
	}

	/**
	 * Update the user's torrent notifications nav item to show whether or not
	 * there are new notifications, or hide the item if they don't have notifications
	 */
	private void updateTorrentNotifications(SharedPreferences preferences){
		if (MySoup.isNotificationsEnabled()){
			int newNotifications = preferences.getInt(getString(R.string.key_pref_num_notifications), 0);
			if (newNotifications > 0){
				adapter.fuzzyUpdate(getString(R.string.notifications),
					Integer.toString(newNotifications) + " " + getString(R.string.notifications));
			}
			else {
				adapter.fuzzyUpdate(getString(R.string.notifications), getString(R.string.notifications));
			}
		}
		else {
			adapter.remove(getString(R.string.notifications));
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Update the user's subscriptions nav item to show whether or not there
	 * are new subscriptions
	 */
	private void updateSubscriptions(SharedPreferences preferences){
		boolean newSubscriptions = preferences.getBoolean(getString(R.string.key_pref_new_subscriptions), false);
		if (newSubscriptions){
			adapter.fuzzyUpdate(getString(R.string.subscriptions), getString(R.string.new_subscriptions));
		}
		else {
			adapter.fuzzyUpdate(getString(R.string.subscriptions), getString(R.string.subscriptions));
		}
		adapter.notifyDataSetChanged();
	}

	public boolean isDrawerOpen(){
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerview);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout){
		fragmentContainerview = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(getActivity(), NavigationDrawerFragment.this.drawerLayout,
			R.drawable.ic_drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView){
				super.onDrawerClosed(drawerView);
				if (!isAdded()){
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView){
				super.onDrawerOpened(drawerView);
				if (!isAdded()){
					return;
				}

				if (!userLearnedDrawer){
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					userLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		//If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		if (!userLearnedDrawer && !fromSavedState){
			this.drawerLayout.openDrawer(fragmentContainerview);
		}
		this.drawerLayout.post(new Runnable() {
			@Override
			public void run(){
				drawerToggle.syncState();
			}
		});
		this.drawerLayout.setDrawerListener(drawerToggle);
	}

	private void selectItem(int position){
		selectedPos = position;
		if (listView != null){
			listView.setItemChecked(position, true);
		}
		if (drawerLayout != null){
			drawerLayout.closeDrawer(fragmentContainerview);
		}
		if (drawerCallbacks != null){
			drawerCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	/**
	 * Get some string stored in the adapter
	 *
	 * @param position the item to get
	 * @return the string at the position
	 */
	public String getItem(int position){
		return adapter.getItem(position);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			drawerCallbacks = (NavigationDrawerCallbacks) activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach(){
		super.onDetach();
		drawerCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, selectedPos);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		//Forward the new configuration the drawer toggle component.
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		//If the drawer is open, show the global app actions in the action bar. See also
		//showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (drawerLayout != null && isDrawerOpen()){
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (drawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		switch (item.getItemId()){
			case R.id.action_settings:
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar(){
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
