package what.gui;

import java.text.DecimalFormat;

import what.settings.Settings;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class MyActivity extends SherlockFragmentActivity {
	private static int DEFAULT_THEME = com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow;
	private static DisplayMetrics displaymetrics = null;
	private static BitmapDrawable customBackground;
	private static String customBackgroundPath = "";
	private static int screenHeight, screenWidth;
	private GestureLibrary gestureLib;
	private DecimalFormat df = new DecimalFormat("#.00");
	private View v;
	private static String image_id = "";
	private GestureOverlayView gestureOverlayView;
	private static InputMethodManager mgr;
	private static boolean isGesturesEnabled;
	private float gestureSensitivity;
	protected Bundle myBundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDisplayMetrics();

		if ((Settings.getSettings() == null) | (Settings.getSettingsEditor() == null)) {
			Settings.init(this);
		}

		try {
			isGesturesEnabled = Settings.getGesturesEnabled();
		} catch (Exception e) {
			e.printStackTrace();
		}

		myBundle = getIntent().getExtras();
		init();
	}

	public void bundle() {
	}

	@Override
	public void setTheme(int resid) {
		super.setTheme(resid);
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

	public void setButtonState(Button button, boolean b) {
		button.setEnabled(b);
		if (b == true) {
			button.setTextAppearance(this, R.style.ButtonTextSmall);
		} else {
			button.setTextColor(Color.BLACK);
		}
	}

	public void showSoftKeyboard(View view) {
		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	public void hideSoftKeyboard(View view) {
		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * Reload the current activity.
	 */
	public void refresh() {
		finish();
		startActivity(getIntent());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setDisplayMetrics() {
		if (displaymetrics == null) {
			displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			screenHeight = displaymetrics.heightPixels;
			screenWidth = displaymetrics.widthPixels;
		}
	}

	public int getHeight() {
		return screenHeight;
	}

	public int getWidth() {
		return screenWidth;
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

	public String toGBString(String s) {
		double d = Double.parseDouble(s) / Math.pow(1024, 3);
		return df.format(d);
	}

	public String toGBString(Double s) {
		double d = s / Math.pow(1024, 3);
		return df.format(d);
	}

	public String toGBString(int s) {
		double d = s / Math.pow(1024, 3);
		return df.format(d);
	}

	public String cleanTags(String post) {
		return post.replace("[img]", "").replace("[/img]", "");
	}

	public void enableGestures(boolean b) {
            if (gestureOverlayView != null)
		gestureOverlayView.setEnabled(b);
	}

	public void onRefreshGesturePerformed() {
		refresh();
		Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();

	}

	public void onHomeGesturePerformed() {
		// Intent intent = new Intent(MyActivity.this, what.home.HomeActivity.class);
		// startActivity(intent);
	}

	public void onRightGesturePerformed() {

	}

	public void onLeftGesturePerformed() {
		finish();
	}

	public void onDownGesturePerformed() {

	}

	public void onUpGesturePerformed() {

	}

}
