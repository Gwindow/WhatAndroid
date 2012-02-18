package what.gui;

import java.text.DecimalFormat;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MyTabActivity extends TabActivity {
	private static DisplayMetrics displaymetrics = null;
	private static int screenHeight, screenWidth;
	private GestureDetector gestureDetector;
	private DecimalFormat df = new DecimalFormat("#.00");
	private View v;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);
		setDisplayMetrics();
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

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureDetector.onTouchEvent(me);
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
