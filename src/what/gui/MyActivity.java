package what.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;

import what.settings.Settings;
import android.app.Activity;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MyActivity extends Activity implements OnGesturePerformedListener {
	private static final String GESTURE_UP = "top";
	private static final String GESTURE_DOWN = "bottom";
	private static final String GESTURE_LEFT = "left";
	private static final String GESTURE_RIGHT = "right";
	private static final String GESTURE_REFRESH = "refresh";
	private static final String GESTURE_HOME = "home";

	private static DisplayMetrics displaymetrics = null;
	private static int screenHeight, screenWidth;
	private static final boolean customBackgroundLoaded = true;
	private static String customBackgroundPath = "";
	private static Drawable customBackgroundDrawable;
	private GestureLibrary gestureLib;
	private DecimalFormat df = new DecimalFormat("#.00");
	private View v;
	private static boolean resizeBackgroundEnabled = true;
	private static BitmapDrawable resizedBackground;
	private static String image_id = "";
	private GestureOverlayView gestureOverlayView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);

		setDisplayMetrics();
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
		gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(layoutResID, null);
		gestureOverlayView.addView(inflate);
		gestureOverlayView.addOnGesturePerformedListener(this);
		gestureOverlayView.setGestureVisible(true);
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load()) {
			finish();
		}

		super.setContentView(gestureOverlayView);

		if (enableBackground) {
			v = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
			if (Settings.getCustomBackground()) {
				loadCustomBackground();
			} else {
				loadDefaultBackground();
			}
		}
	}

	private void loadDefaultBackground() {
		try {
			setAndResizeBackground(R.drawable.bricks_background);
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

	public void onCreate(Bundle savedInstanceState, int layoutReference, boolean enableBackground) {
		super.onCreate(savedInstanceState);
		// set the background
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, what.gui.MainMenu.class);
			startActivityForResult(intent, 0);
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
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		for (Prediction prediction : predictions) {
			if (prediction.score > 2.0) {
				if (prediction.name.trim().equals(GESTURE_UP)) {
					onUpGesturePerformed();
				}
				if (prediction.name.trim().equals(GESTURE_DOWN)) {
					onDownGesturePerformed();
				}
				if (prediction.name.trim().equals(GESTURE_LEFT)) {
					onLeftGesturePerformed();
				}
				if (prediction.name.trim().equals(GESTURE_RIGHT)) {
					onRightGesturePerformed();
				}
				if (prediction.name.trim().equals(GESTURE_REFRESH)) {
					onRefreshGesturePerformed();
				}
				if (prediction.name.trim().equals(GESTURE_HOME)) {
					onHomeGesturePerformed();
				}
			}
		}
	}

	public void onRefreshGesturePerformed() {
		finish();
		startActivity(getIntent());
		Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();

	}

	public void onHomeGesturePerformed() {
		Intent intent = new Intent(MyActivity.this, what.home.HomeActivity.class);
		startActivity(intent);
	}

	public void onRightGesturePerformed() {

	}

	public void onLeftGesturePerformed() {

	}

	public void onDownGesturePerformed() {
		finish();
	}

	public void onUpGesturePerformed() {

	}

}
