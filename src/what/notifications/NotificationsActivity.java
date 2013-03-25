package what.notifications;

import java.util.List;

import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.settings.Settings;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.notifications.Notifications;
import api.notifications.Torrent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 7, 2012 11:43:17 PM
 */

public class NotificationsActivity extends MyActivity2 implements OnClickListener, Scrollable {
	private static final int TORRENTGROUP_TAG = 0;
	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private Notifications notifications;
	private int notificationsPage = 1;

	private boolean isLoaded;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.NOTIFICATIONS);
		super.requestIndeterminateProgress();
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.generic_endless_scrollview, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Settings.saveHomeInfoCounter(0);
		Bundle bundle = getIntent().getExtras();
		try {
			notificationsPage = bundle.getInt(BundleKeys.NOTIFICATIONS_PAGE);
		} catch (Exception e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		scrollView = (MyScrollView) this.findViewById(R.id.scrollView);
		scrollView.attachScrollable(this);
		scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		new Load().execute();
	}

	/**
     * TODO: I'd like to show the artist name as well, I feel like it's kind of important to the info
	 * Populate notifications.
	 */
	private void populate() {
		setActionBarTitle("Notifications, " + notificationsPage + "/" + notifications.getResponse().getPages());
		List<Torrent> results = notifications.getResponse().getResults();

		if (results != null) {
			for (Torrent torrent : results) {
				TextView torrentgroup_title =
                    (TextView) getLayoutInflater().inflate(R.layout.notifications_torrentgroup_title, null);
				String title = torrent.getGroupName() + " - " + torrent.getMediaFormatEncoding();
				String displayed_title = torrent.isUnread() ? "New! " + title : title;

				torrentgroup_title.setText(displayed_title);
				torrentgroup_title.setId(torrent.getGroupId().intValue());
				torrentgroup_title.setTag(TORRENTGROUP_TAG);
				torrentgroup_title.setOnClickListener(this);

                scrollLayout.addView(torrentgroup_title);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrolledToBottom() {
		nextPage();
	}

	/**
	 * Load the next page while currentPage < totalPages.
	 */
	private void nextPage() {
		if (isLoaded) {
			if (notificationsPage < notifications.getLastPage()) {
				notificationsPage++;
				new Load(true).execute();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case TORRENTGROUP_TAG:
				openTorrentGroup(v.getId());
				break;
			default:
				break;
		}
	}

	private void openTorrentGroup(int id) {
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.TORRENT_GROUP_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	private void clearNotifications() {
		notifications.clearNotifications();
		refresh();
		Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show();
	}

	private void jumpToPage() {
		new JumpToPageDialog(this, notifications.getResponse().getPages()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.INBOX_PAGE, getPage());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.notifications_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.clear_item:
				clearNotifications();
				break;
		}
		if (item.getItemId() == android.R.id.home) {
			return homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			this(false);
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(NotificationsActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			notifications = Notifications.notificationsFromPage(notificationsPage);
			return notifications.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			isLoaded = true;
			if (useEmbeddedDialog) {
				hideProgressBar();
			} else {
				hideIndeterminateProgress();
				unlockScreenRotation();
			}

			if (status) {
				populate();
			}
			if (!status) {
				ErrorToast.show(NotificationsActivity.this, NotificationsActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}
}
