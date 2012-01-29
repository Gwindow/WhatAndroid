package what.gui;

import java.text.DecimalFormat;

import what.settings.Settings;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MyActivity extends Activity implements OnGestureListener {
	private static DisplayMetrics displaymetrics = null;
	private static final boolean customBackgroundLoaded = true;
	private static String customBackgroundPath = "";
	private static Drawable customBackgroundDrawable;
	private GestureDetector gestureDetector;
	private int height, width;
	private DecimalFormat df = new DecimalFormat("#.00");
	private View v;
	private static BitmapDrawable resizedBackground;
	private static String image_id = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);
		gestureDetector = new GestureDetector(this);
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
			setAndResizeBackground(R.drawable.rain_background);
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
			resizedBackground = new BitmapDrawable(bitmap);
			v.setBackgroundDrawable(resizedBackground);
			image_id = new_id;
		} else {
			v.setBackgroundDrawable(resizedBackground);
		}
	}

	private void setAndResizeBackground(int new_id) {
		if (!image_id.equals(String.valueOf(new_id))) {
			Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), new_id);
			bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(), true);
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
			height = displaymetrics.heightPixels;
			width = displaymetrics.widthPixels;
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
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

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureDetector.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
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
}
