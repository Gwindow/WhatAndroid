package what.whatandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import api.soup.MySoup;
import what.whatandroid.home.HomeFragment;
import what.whatandroid.login.LoginFragment;

public class WhatAndroid extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentHost {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navDrawer;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	/*
	Developers: I recommend running your own install of Gazelle locally to test the app against
	instead of working with the real site. You can get Gazelle here: http://whatcd.github.io/Gazelle/
	Put your local Gazelle IP here to connect to the site
	*/
	private static final String site = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navDrawer = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		navDrawer.setUp(
			R.id.navigation_drawer,
			(DrawerLayout) findViewById(R.id.drawer_layout));

		MySoup.setSite(site, false);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		//TODO: This should be designed better
		if (navDrawer == null){
			fragmentManager.beginTransaction()
				.replace(R.id.container, new LoginFragment())
				.commit();
			mTitle = getString(R.string.login);
		}
		else if (navDrawer.getNavElement(position).equalsIgnoreCase(LoginFragment.NAME)){
			fragmentManager.beginTransaction()
				.replace(R.id.container, new LoginFragment())
				.commit();
			mTitle = LoginFragment.NAME;
		}
		else if (navDrawer.getNavElement(position).equalsIgnoreCase(HomeFragment.NAME)){
			fragmentManager.beginTransaction()
				.replace(R.id.container, new LoginFragment())
				.commit();
			mTitle = HomeFragment.NAME;
		}
	}

	@Override
	public void replaceFragment(Fragment fragment, String title, Boolean removeNav) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);

		if (removeNav){
			navDrawer.removeNavElement(getString(R.string.login));
			navDrawer.addNavElement(title);
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
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
}
