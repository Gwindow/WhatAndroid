package what.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;

import what.settings.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
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

public class MyActivity extends Activity implements OnGesturePerformedListener {

	private static DisplayMetrics displaymetrics = null;
	private static boolean resizeBackgroundEnabled = true;
	private static BitmapDrawable resizedBackground;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDisplayMetrics();

		if (Settings.getSettings() == null | Settings.getSettingsEditor() == null) {
			Settings.init(this);
		}

		try {
			isGesturesEnabled = Settings.getGesturesEnabled();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the content view with background
	 * 
	 * @param layoutResID
	 *            the layout res id
	 * @param enableBackground
	 *            the enable background
	 */
	public void setContentView(int layoutResID, boolean enableBackground) {
		if (isGesturesEnabled) {
			super.setContentView(loadGestureOverLayView(layoutResID));
		} else {
			super.setContentView(layoutResID);
		}
		if (enableBackground) {
			v = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
			if (Settings.getCustomBackground()) {
				loadCustomBackground();
			} else {
				loadDefaultBackground();
			}
		}
	}

	private GestureOverlayView loadGestureOverLayView(int layoutResID) {
		gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(layoutResID, null);
		gestureOverlayView.addView(inflate);
		gestureOverlayView.addOnGesturePerformedListener(this);
		gestureOverlayView.setGestureVisible(true);
		gestureOverlayView.setGestureStrokeWidth(3.0f);
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		gestureSensitivity = Settings.getGestureSensitivity();
		if (!gestureLib.load()) {
			finish();
		}
		return gestureOverlayView;
	}

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
			if (!customBackgroundPath.equalsIgnoreCase(Settings.getCustomBackgroundPath())) {
				String path = (Settings.getCustomBackgroundPath());
				setAndResizeBackground(path);
				customBackgroundPath = path;
			}
		} catch (Exception e) {
			e.printStackTrace();
			v.setBackgroundColor(R.color.black);
			Toast.makeText(this, "custom background failed", Toast.LENGTH_SHORT).show();

		}
	}

	private void setAndResizeBackground(String new_id) {
		if (!image_id.equals(new_id)) {
			Bitmap bitmap = BitmapFactory.decodeFile(new_id);
			bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(), true);
			if (resizeBackgroundEnabled) {
				resizedBackground = new BitmapDrawable(bitmap);
				v.setBackgroundDrawable(resizedBackground);
			}
			image_id = new_id;
		} else {
			v.setBackgroundDrawable(resizedBackground);
		}
	}

	private void setAndResizeBackground(int new_id) {
		if (!image_id.equals(String.valueOf(new_id))) {
			Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), new_id);
			if (resizeBackgroundEnabled) {
				bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(), true);
			}
			resizedBackground = new BitmapDrawable(bitmap);
			v.setBackgroundDrawable(resizedBackground);
			image_id = String.valueOf(new_id);
		} else {
			v.setBackgroundDrawable(resizedBackground);
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
			Intent intent = new Intent(this, what.gui.MainMenu.class);
			startActivity(intent);
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

	/**
	 * @return the resizeBackgroundEnabled
	 */
	public static boolean isResizeBackgroundEnabled() {
		return resizeBackgroundEnabled;
	}

	/**
	 * @param resizeBackgroundEnabled
	 *            the resizeBackgroundEnabled to set
	 */
	public static void setResizeBackgroundEnabled(boolean resizeBackgroundEnabled) {
		MyActivity.resizeBackgroundEnabled = resizeBackgroundEnabled;
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		if (isGesturesEnabled) {
			ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
			for (Prediction prediction : predictions) {
				if (prediction.score > gestureSensitivity) {
					if (prediction.name.trim().equals(Gestures.UP)) {
						onUpGesturePerformed();
					}
					if (prediction.name.trim().equals(Gestures.DOWN)) {
						onDownGesturePerformed();
					}
					if (prediction.name.trim().equals(Gestures.LEFT)) {
						onLeftGesturePerformed();
					}
					if (prediction.name.trim().equals(Gestures.RIGHT)) {
						onRightGesturePerformed();
					}
					if (prediction.name.trim().equals(Gestures.REFRESH)) {
						onRefreshGesturePerformed();
					}
					if (prediction.name.trim().equals(Gestures.HOME)) {
						onHomeGesturePerformed();
					}
				}
				if (prediction.score > 9 || prediction.score > (gestureSensitivity + 1)) {
					if (prediction.name.trim().equals(Gestures.MENU)) {
						onMenuGesturePerformed();
					}
				}
			}
		}
	}

	public void enableGestures(boolean b) {
		gestureOverlayView.setEnabled(b);
	}

	public void onMenuGesturePerformed() {
		Intent intent = new Intent(MyActivity.this, what.gui.MainMenu.class);
		startActivity(intent);
	}

	public void onRefreshGesturePerformed() {
		refresh();
		Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();

	}

	public void onHomeGesturePerformed() {
		Intent intent = new Intent(MyActivity.this, what.home.HomeActivity.class);
		startActivity(intent);
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
