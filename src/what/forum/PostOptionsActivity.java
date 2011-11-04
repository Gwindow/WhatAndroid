package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

public class PostOptionsActivity extends MyActivity {
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postoptions);

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		getBundle();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getBundle() {
	}

	public void quotePost(View v) {
	}

	public void showPostTime(View c) {
	}

	public void openProfile(View v) {
	}

}
