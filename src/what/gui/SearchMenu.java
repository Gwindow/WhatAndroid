package what.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Gwindow
 * 
 */
public class SearchMenu extends MyActivity {
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.search_menu);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
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
		startActivity(intent);
	}

	public void requests(View v) {
		intent = new Intent(SearchMenu.this, what.search.RequestsSearchActivity.class);
		startActivity(intent);
	}

	public void barcode(View v) {
		intent = new Intent(SearchMenu.this, what.barcode.ScannerActivity.class);
		startActivity(intent);
	}

	public void users(View v) {
		intent = new Intent(SearchMenu.this, what.search.UserSearchActivity.class);
		startActivity(intent);
	}

	public void goggles(View v) {
		intent = new Intent(SearchMenu.this, what.goggles.GogglesSearchActivity.class);
		startActivity(intent);
	}
}