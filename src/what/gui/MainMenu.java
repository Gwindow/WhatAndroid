/**
 * 
 */
package what.gui;

import what.settings.Settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import api.soup.MySoup;

/**
 * 
 */
public class MainMenu extends MyActivity {
	private Intent intent;
	private Button debugButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.menu_main);
	}

	@Override
	public void init() {
	}

	@Override
	public void load() {
		debugButton = (Button) this.findViewById(R.id.button11);
	}

	@Override
	public void prepare() {
		if (Settings.getDebugPreference()) {
			debugButton.setVisibility(View.VISIBLE);
		}

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

	public void search(View v) {
		intent = new Intent(MainMenu.this, what.gui.SearchMenu.class);
		startActivity(intent);
	}

	public void forum(View v) {
		intent = new Intent(MainMenu.this, what.forum.ForumSectionsListActivity.class);
		startActivity(intent);
	}

	public void notifications(View v) {
		if (MySoup.canNotifications()) {
			intent = new Intent(MainMenu.this, what.notifications.NotificationsActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(this, "You need to be a power user or higher to access notifications", Toast.LENGTH_LONG).show();
		}
	}

	public void inbox(View v) {
		intent = new Intent(MainMenu.this, what.inbox.InboxActivity.class);
		startActivity(intent);
	}

	public void bookmarks(View v) {
		intent = new Intent(MainMenu.this, what.bookmarks.TorrentBookmarksActivity.class);
		startActivity(intent);
	}

	public void settings(View v) {
		intent = new Intent(MainMenu.this, what.settings.SettingsActivity.class);
		startActivity(intent);
	}

	public void home(View v) {
		intent = new Intent(MainMenu.this, what.home.HomeActivity.class);
		startActivity(intent);
	}

	public void status(View v) {
		intent = new Intent(MainMenu.this, what.status.WhatStatusActivity.class);
		startActivity(intent);
	}

	public void topten(View v) {
		intent = new Intent(MainMenu.this, what.top.TopTorrentsActivity.class);
		startActivity(intent);
	}

	public void debug(View v) {
		intent = new Intent(MainMenu.this, what.debug.DebugActivity.class);
		startActivity(intent);
	}

	public void donate(View v) {
		/* intent = new Intent(MainMenu.this, what.donate.FlattrDonationActivity.class); startActivityForResult(intent,
		 * 0); */
		Toast.makeText(MainMenu.this, "I don't know yet", Toast.LENGTH_SHORT).show();
	}
}
