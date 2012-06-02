package what.gui;

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
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public abstract class MyActivity2 extends SherlockFragmentActivity {
	private static final int THEME = R.style.LightTheme;
	private static final int MENU_PLACEHOLDER_ID = 1;
	private static final int MENU_ITEM_ID = 2;
	private View v;
	private String activityName;
	private TextView actionBarTitle;
	private DisplayMetrics metrics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setTheme(THEME);
		super.onCreate(savedInstanceState);

		if ((Settings.getSettings() == null) | (Settings.getSettingsEditor() == null)) {
			Settings.init(this);
		}

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		init();

		getSupportActionBar().setDisplayShowHomeEnabled(false);
		this.getSupportActionBar().setDisplayShowCustomEnabled(true);
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflator.inflate(R.layout.actionbar_title, null);
		actionBarTitle = ((TextView) v.findViewById(R.id.title));

		getSupportActionBar().setCustomView(v);
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
		switch (this.getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
		}
	}

	public void unlockScreenRotation() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
		// this first item is a dummy menu item used to identify the currently selected item
		SubMenu submenu = menu.addSubMenu(Menu.NONE, MENU_PLACEHOLDER_ID, Menu.NONE, activityName);
		submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.HOME.toString());
		submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.FORUM.toString());

		SubMenu searchmenu = submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.SEARCH.toString());
		searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.TORRENTS.toString());
		searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.REQUESTS.toString());
		searchmenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.USERS.toString());

		submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.INBOX.toString());
		submenu.addSubMenu(Menu.NONE, MENU_ITEM_ID, Menu.NONE, MenuItems.BARCODE_SCANNER.toString());

		MenuItem subMenuItem = submenu.getItem();
		// subMenuItem.setIcon(R.drawable.ic_title_share_default);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

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
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @return the metrics
	 */
	public DisplayMetrics getMetrics() {
		return metrics;
	}

}
