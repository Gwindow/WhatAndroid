/**
 * 
 */
package what.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import api.soup.MySoup;

/**
 * 
 */
public class MainMenu extends MyActivity {
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_main);
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

	public void search(View v) {
		intent = new Intent(MainMenu.this, what.gui.SearchMenu.class);
		startActivityForResult(intent, 0);
	}

	public void forum(View v) {
		intent = new Intent(MainMenu.this, what.forum.ForumSectionsListActivity.class);
		startActivityForResult(intent, 0);
	}

	public void notifications(View v) {
		if (MySoup.canNotifications()) {
			intent = new Intent(MainMenu.this, what.notifications.NotificationsActivity.class);
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(this, "You need to be a power user or higher to access notifications", Toast.LENGTH_LONG).show();
		}
	}

	public void inbox(View v) {
		intent = new Intent(MainMenu.this, what.inbox.InboxActivity.class);
		startActivityForResult(intent, 0);
	}

	public void bookmarks(View v) {
		intent = new Intent(MainMenu.this, what.bookmarks.TorrentBookmarksActivity.class);
		startActivityForResult(intent, 0);
	}

	public void settings(View v) {
		intent = new Intent(MainMenu.this, what.settings.SettingsActivity.class);
		startActivityForResult(intent, 0);
	}

	public void home(View v) {
		intent = new Intent(MainMenu.this, what.home.HomeActivity.class);
		startActivityForResult(intent, 0);
	}

	public void status(View v) {
		intent = new Intent(MainMenu.this, what.status.StatusActivity.class);
		startActivityForResult(intent, 0);
	}

	public void topten(View v) {
		intent = new Intent(MainMenu.this, what.top.TopTorrentsActivity.class);
		startActivityForResult(intent, 0);
	}

	public void donate(View v) {
		intent = new Intent(MainMenu.this, what.donate.FlattrDonationActivity.class);
		startActivityForResult(intent, 0);
	}
}
