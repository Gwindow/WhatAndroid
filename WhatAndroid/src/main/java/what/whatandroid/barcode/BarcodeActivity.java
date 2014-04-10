package what.whatandroid.barcode;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import org.apache.commons.io.IOUtils;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.settings.SettingsActivity;

import java.io.InputStream;

/**
 * Activity for receiving intents for loading barcodes, viewing scanned barcodes
 * and launching searches with the terms
 */
public class BarcodeActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private NavigationDrawerFragment navDrawer;
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		navDrawer = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
		title = getTitle();

		if (savedInstanceState == null){
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new BarcodeFragment())
				.commit();
		}

		//Need to also perform some validation to make sure we're getting valid content from ZXing
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/csv")){
			System.out.println("Received action send");
			Uri history = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			ContentResolver resolver = getContentResolver();
			if (history != null){
				try {
					InputStream in = resolver.openInputStream(history);
					System.out.println(IOUtils.toString(in));
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public void restoreActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (navDrawer == null || !navDrawer.isDrawerOpen()){
			getMenuInflater().inflate(R.menu.what_android, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent;
		switch (item.getItemId()){
			case R.id.action_settings:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_feedback:
				intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "whatcdandroid@gmail.com", null));
				intent.putExtra(Intent.EXTRA_SUBJECT, "WhatAndroid Feedback");
				startActivity(Intent.createChooser(intent, "Send email"));
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			Intent intent = new Intent(this, ProfileActivity.class);
			//We may not be logged in at this point, so pass -1 which will indicate to the fragment
			//to get the id from mysoup
			intent.putExtra(ProfileActivity.USER_ID, -1);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
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
}
