package what.gui;

import what.settings.Settings;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MyTabActivity extends TabActivity {
	private static DisplayMetrics displaymetrics = null;
	private static int screenHeight, screenWidth;
	private static String customBackgroundPath = "";
	private View v;
	private static boolean resizeBackgroundEnabled = true;
	private static BitmapDrawable resizedBackground;
	private static String image_id = "";

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
		super.setContentView(layoutResID);
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
		MyTabActivity.resizeBackgroundEnabled = resizeBackgroundEnabled;
	}
}