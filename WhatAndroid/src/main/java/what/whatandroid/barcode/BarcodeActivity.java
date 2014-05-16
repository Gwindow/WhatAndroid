package what.whatandroid.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import api.barcode.Barcode;
import api.products.ProductSearch;
import api.soup.MySoup;
import what.whatandroid.NavigationDrawerFragment;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.bookmarks.BookmarksActivity;
import what.whatandroid.callbacks.ViewSearchCallbacks;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.search.SearchActivity;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.settings.SettingsFragment;

import java.util.Date;

/**
 * Activity for receiving intents for loading barcodes, viewing scanned barcodes
 * and launching searches with the terms
 */
public class BarcodeActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
	ScannerDialog.ScannerDialogListener, ViewSearchCallbacks {

	private NavigationDrawerFragment navDrawer;
	private CharSequence title;
	private BarcodeFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_frame);
		navDrawer = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		navDrawer.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
		title = getTitle();
		navDrawer.updateNotifications(PreferenceManager.getDefaultSharedPreferences(this));

		if (savedInstanceState == null){
			fragment = new BarcodeFragment();
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment).commit();
		}
		else {
			fragment = (BarcodeFragment)getSupportFragmentManager().findFragmentById(R.id.container);
		}

		//Setup our Semantics3 fallback API info
		ProductSearch.setCredentials("",
			"", false);
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
			if (!MySoup.isLoggedIn()){
				MenuItem logout = menu.findItem(R.id.action_logout);
				logout.setVisible(false);
			}
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent;
		switch (item.getItemId()){
			case R.id.action_logout:
				new LogoutTask().execute();
				return true;
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
	public void startSearch(int type, String terms, String tags){
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtra(SearchActivity.SEARCH, type);
		intent.putExtra(SearchActivity.TERMS, terms);
		intent.putExtra(SearchActivity.TAGS, tags);
		startActivity(intent);
	}

	@Override
	public void startSingleScan(){
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		//If ZXing is installed launch it, otherwise ask the user to install it
		if (!getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()){
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		}
		else {
			noScannerAlert();
		}
	}

	@Override
	public void startBulkScan(){
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.zxing.client.android");
		if (intent == null){
			noScannerAlert();
		}
		else {
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(intent);
		}
	}

	/**
	 * Display a dialog to the user that they don't have the required scanner installed
	 * and prompt them to install it
	 */
	private void noScannerAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
			android.R.style.Theme_Holo_Dialog));
		builder.setTitle("ZXing Barcode Scanner Not Found")
			.setMessage("The ZXing Barcode scanner is required to use the scanning features, would you like to install it?")
			.setPositiveButton("Install", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android"));
					startActivity(intent);
				}
			})
			.setNegativeButton("Not now", null);
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == 0){
			if (resultCode == Activity.RESULT_OK){
				fragment.addBarcode(new Barcode(data.getStringExtra("SCAN_RESULT"), new Date()));
			}
			else if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
			}
		}
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
		else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))){
			Intent intent = new Intent(this, BookmarksActivity.class);
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

	/**
	 * Async task for logging out the user
	 */
	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute(){
			dialog = new ProgressDialog(BarcodeActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Logging out...");
			dialog.show();
		}

		/**
		 * Once we've logged out clear the saved cookie, name and password and head to the home screen
		 */
		@Override
		protected void onPostExecute(Boolean status){
			if (dialog.isShowing()){
				dialog.dismiss();
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BarcodeActivity.this);
			preferences.edit()
				.remove(SettingsFragment.USER_COOKIE)
				.remove(SettingsFragment.USER_NAME)
				.remove(SettingsFragment.USER_PASSWORD)
				.commit();

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		@Override
		protected Boolean doInBackground(Void... params){
			Boolean status = false;
			try {
				status = MySoup.logout("logout.php");
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return status;
		}

		public void dismissDialog(){
			if (dialog.isShowing()){
				dialog.dismiss();
			}
		}
	}
}
