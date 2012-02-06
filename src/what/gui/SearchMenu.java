package what.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Tim
 * 
 */
public class SearchMenu extends MyActivity {
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_menu);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void torrents(View v) {
		intent = new Intent(SearchMenu.this, what.search.TorrentSearchActivity.class);
		startActivityForResult(intent, 0);
	}

	public void requests(View v) {
		intent = new Intent(SearchMenu.this, what.search.RequestsSearchActivity.class);
		startActivityForResult(intent, 0);
	}

	public void barcode(View v) {
		intent = new Intent(SearchMenu.this, what.barcode.ScannerActivity.class);
		startActivityForResult(intent, 0);
	}

	public void users(View v) {
		intent = new Intent(SearchMenu.this, what.search.UserSearchActivity.class);
		startActivityForResult(intent, 0);
	}
}