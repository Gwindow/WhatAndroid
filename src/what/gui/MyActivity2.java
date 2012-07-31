package what.gui;

import what.home.HomeActivity;
import what.settings.Settings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;

public abstract class MyActivity2 extends SherlockFragmentActivity {
	private static final int MENU_PLACEHOLDER_ID = 1;
	private static final int MENU_ITEM_ID = 2;
	private View v;
	private String activityName;
	private TextView actionBarTitle;
	private DisplayMetrics metrics;
	private long actionBarTitleTouchedTime;
	private boolean touchToHome = true;
	private Cancelable cancelable;

	public void onCreate(Bundle savedInstanceState, Integer customTheme) {
		setTheme(customTheme);
		super.onCreate(savedInstanceState);
		new ReportSender(this);
		if ((Settings.getSettings() == null) || (Settings.getSettingsEditor() == null)) {
			Settings.init(this);
		}

		metrics = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		getSupportActionBar().setLogo(Settings.getHomeIconPath());
		getSupportActionBar().setDisplayShowHomeEnabled(Settings.getHomeIcon());
		getSupportActionBar().setDisplayHomeAsUpEnabled(Settings.getHomeIcon());
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflator.inflate(R.layout.actionbar_title, null);
		actionBarTitle = ((TextView) v.findViewById(R.id.title));

		getSupportActionBar().setCustomView(v);
		init();
	}

	@Override
	public void onResume() {
		if ((Settings.getSettings() == null) || (Settings.getSettingsEditor() == null)) {
			Settings.init(this);
		}
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, null);
	}

	private void setTheme(Integer customTheme) {
		int theme = customTheme == null ? Settings.getTheme() : customTheme;
		super.setTheme(theme);
	}

	/**
	 * Initialize variables and anything that must be done first.
	 */
	public abstract void init();

	/**
	 * Sets the content view with background
	 * 
	 * @param layoutResID
	 *            the layout res id
	 * @param enableBackground
	 *            the enable background
	 */
	public void setContentView(int layoutResID, boolean enableBackground) {
		super.setContentView(layoutResID);
		if (enableBackground) {
			v = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
			if (Settings.getCustomBackground()) {
				loadCustomBackground();
			} else {
				loadDefaultBackground();
			}
		}
		load();
		prepare();
		actionbar();
	}

	@Override
	public void setContentView(int layoutResID) {
		setContentView(layoutResID, false);
	}

	/**
	 * Find resources, setting additional properties such as gravity or listeners should be done here.
	 */
	public abstract void load();

	/**
	 * Prepare the actionbar for the user.
	 */
	public void actionbar() {

	}

	public void setActionBarTitle(String title) {
		actionBarTitle.setText(title);
	}

	public void setActionBarTouchToHome(boolean touchToHome) {
		this.touchToHome = touchToHome;
	}

	/**
	 * Prepare the activity for the user, run any code necessary to do that here.
	 */
	public abstract void prepare();

	private void loadDefaultBackground() {
		try {
			BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.renzler));
			bd.setTileModeX(Shader.TileMode.REPEAT);
			bd.setTileModeY(Shader.TileMode.REPEAT);
			v.setBackgroundDrawable(bd);
		} catch (Exception e) {
			e.printStackTrace();
			v.setBackgroundColor(R.color.black);
			Toast.makeText(this, "default background failed", Toast.LENGTH_SHORT).show();
		}
	}

	private void loadCustomBackground() {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(Settings.getCustomBackgroundPath());
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			if (Settings.getTileBackground()) {
				bd.setTileModeX(Shader.TileMode.REPEAT);
				bd.setTileModeY(Shader.TileMode.REPEAT);
			}
			v.setBackgroundDrawable(bd);
		} catch (Exception e) {
			e.printStackTrace();
			v.setBackgroundColor(R.color.black);
			Toast.makeText(this, "default background failed", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Reload the current activity.
	 */
	public void refresh() {
		finish();
		startActivity(getIntent());
	}

	public void lockScreenRotation() {
		switch (getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				}
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				}
				break;

		}
	}

	public void unlockScreenRotation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	public void openHome(View v) {
		if (touchToHome) {
			if ((System.currentTimeMillis() - actionBarTitleTouchedTime) <= 500) {
				startActivity(new Intent(this, HomeActivity.class));
				actionBarTitleTouchedTime = 0;
			} else {
				actionBarTitleTouchedTime = System.currentTimeMillis();
			}
		}
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		if (activityName == null) {
			activityName = "";
		}
		return activityName;
	}

	public void setActivityName(ActivityNames activityName) {
		setActivityName(activityName.toString());
	}

	/**
	 * @param activityName
	 *            the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySoup.isLoggedIn()) {
			// this first item is a dummy menu item used to identify the currently selected item
			SubMenu submenu = menu.addSubMenu(Menu.NONE, MENU_PLACEHOLDER_ID, Menu.NONE, activityName);
			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.HOME.toString());
			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.FORUM.toString());

			SubMenu searchmenu = submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.SEARCH.toString());
			searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.TORRENTS.toString());
			searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.REQUESTS.toString());
			searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.USERS.toString());

			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.INBOX.toString());
			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.BOOKMARKS.toString());
			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.NOTIFICATIONS.toString());
			submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.BARCODE_SCANNER.toString());

			SubMenu moremenu = submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.MORE.toString());
			moremenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.SETTINGS.toString());
			moremenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.TOP_TEN.toString());
			moremenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.STATUS.toString());
			if (Settings.getDebugPreference()) {
				moremenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.DEBUG.toString());
			}

			MenuItem subMenuItem = submenu.getItem();
			// subMenuItem.setIcon(R.drawable.ic_title_share_default);
			subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_ITEM_ID) {
			MenuItems mi = MenuItems.valueOf(item.getTitle().toString().toUpperCase().replace(" ", "_"));
			if (MenuItems.containsKey(mi)) {
				Intent intent = new Intent(this, MenuItems.get(mi));
				startActivity(intent);
			}
		}
		/*
		 * if (item.getItemId() == android.R.id.home) { finish(); }
		 */
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @return the metrics
	 */
	public DisplayMetrics getMetrics() {
		return metrics;
	}

	public void enableFade() {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	public void fade() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	public void requestIndeterminateProgress() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminateVisibility(true);
	}

	public void hideIndeterminateProgress() {
		setSupportProgressBarIndeterminateVisibility(false);

	}

	/**
	 * @param scrollView
	 */
	public boolean homeIconJump(MyScrollView scrollView) {
		if (scrollView != null) {
			if (scrollView.getScrollY() <= 0) {
				finish();
			} else {
				scrollView.scrollToTop();
			}
		} else {
			finish();
		}
		return true;
	}

	public void attachCancelable(Cancelable cancelable) {
		this.cancelable = cancelable;
	}

	@Override
	protected void onPause() {
		if (cancelable != null) {
			cancelable.cancel();
		}
		super.onPause();
	}

}
