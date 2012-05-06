package what.home;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import what.services.AnnouncementService;
import what.services.InboxService;
import what.services.NotificationService;
import what.settings.Settings;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import api.soup.MySoup;
import api.subscriptions.Subscriptions;
import api.subscriptions.Threads;

public class HomeActivity extends MyActivity implements OnClickListener, OnEditorActionListener {
	private Subscriptions subscriptions;
	private TextView username, uploadedValue, downloadedValue, ratioValue, bufferValue;
	private EditText searchBar;
	private ArrayList<TextView> threadList;
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private Intent intent;
	private Intent inboxService;
	private Intent notificationService;
	private Intent annoucementService;
	private static String up, down, ratio, buffer;
	private static boolean isCached;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.home, true);
	}

	@Override
	public void init() {
		threadList = new ArrayList<TextView>();
	}

	@Override
	public void load() {
		username = (TextView) this.findViewById(R.id.username);
		uploadedValue = (TextView) this.findViewById(R.id.upvalue);
		downloadedValue = (TextView) this.findViewById(R.id.downvalue);
		ratioValue = (TextView) this.findViewById(R.id.ratiovalue);
		bufferValue = (TextView) this.findViewById(R.id.buffervalue);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		searchBar = (EditText) this.findViewById(R.id.searchBar);
		searchBar.setOnEditorActionListener(this);
	}

	@Override
	public void prepare() {
		// TODO reenable?
		// startServices();
		showFirstRunDialog();

		loadSearchBar();
		loadStats();
		if (Settings.getSubscriptionsEnabled()) {
			new LoadSubscriptions().execute();
		}
	}

	private void showFirstRunDialog() {
		if (Settings.getFirstRun()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Greetings");
			alert.setMessage("Please take a moment to configure settings");
			alert.setPositiveButton("Take me to settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					intent = new Intent(HomeActivity.this, what.settings.SettingsActivity.class);
					startActivity(intent);
				}
			});
			alert.setNegativeButton("Later", null);
			alert.setCancelable(true);
			alert.create().show();
			Settings.saveFirstRun(false);
		}

	}

	private void startServices() {
		inboxService = new Intent(this, InboxService.class);
		annoucementService = new Intent(this, AnnouncementService.class);
		notificationService = new Intent(this, NotificationService.class);

		if ((Settings.getInboxService() == true) && !InboxService.isRunning()) {
			try {
				startService(inboxService);
			} catch (Exception e) {
				Toast.makeText(this, "Could not start inbox service", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		if ((Settings.getAnnouncementsService() == true) && !AnnouncementService.isRunning()) {
			try {
				startService(annoucementService);
			} catch (Exception e) {
				Toast.makeText(this, "Could not start announcements service", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		if ((Settings.getNotificationsService() == true) && !NotificationService.isRunning() && MySoup.canNotifications()) {
			try {
				startService(notificationService);
			} catch (Exception e) {
				Toast.makeText(this, "Could not start notifications service", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

	}

	private void loadStats() {
		if (isCached) {
			username.setText(MySoup.getUsername());
			uploadedValue.setText(up);
			downloadedValue.setText(down);
			ratioValue.setText(ratio);
			bufferValue.setText(buffer);
		} else {
			up = "U: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getUploaded().toString()) + "GB ";
			down = "D: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getDownloaded().toString()) + "GB ";
			ratio = "R: " + MySoup.getIndex().getResponse().getUserstats().getRatio();
			buffer = "B: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getBuffer().toString()) + "GB";

			username.setText(MySoup.getUsername());
			uploadedValue.setText(up);
			downloadedValue.setText(down);
			ratioValue.setText(ratio);
			bufferValue.setText(buffer);

			isCached = true;
		}
	}

	private void loadSearchBar() {
		if (Settings.getQuickSearch() == false) {
			searchBar.setVisibility(EditText.GONE);
		}
	}

	public void openProfile(View v) {
		Bundle b = new Bundle();
		intent = new Intent(HomeActivity.this, what.user.UserProfileTabActivity.class);
		b.putInt("userId", MySoup.getUserId());
		intent.putExtras(b);
		startActivity(intent);
	}

	public void openTorrents(View v) {
		Intent intent = new Intent(this, what.search.TorrentSearchActivity.class);
		startActivity(intent);
	}

	public void openForum(View v) {
		intent = new Intent(HomeActivity.this, what.forum.ForumSectionsListActivity.class);
		startActivity(intent);
	}

	public void openInbox(View v) {
		intent = new Intent(HomeActivity.this, what.inbox.InboxActivity.class);
		startActivity(intent);
	}

	public void openTopTen(View v) {
		intent = new Intent(HomeActivity.this, what.top.TopTorrentsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
		if ((actionId == EditorInfo.IME_NULL) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
			Bundle b = new Bundle();
			intent = new Intent(HomeActivity.this, what.search.TorrentSearchActivity.class);
			b.putString("searchTerm", searchBar.getText().toString());
			b.putString("tagSearchTerm", " ");
			intent.putExtras(b);
			startActivity(intent);
			hideSoftKeyboard(searchBar);
		}
		return true;
	}

	public void refresh(View v) {
		scrollLayout.removeAllViews();
		threadList.clear();
		new LoadSubscriptions() {
			@Override
			protected void onPostExecute(Boolean status) {
				if (status == true) {
					populateLayout();
					Toast.makeText(HomeActivity.this, "Subscriptions Refreshed", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
				if (status == false) {
					Toast.makeText(HomeActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
				}
				unlockScreenRotation();
			}
		}.execute();
	}

	private void openThread(int i) {
		Bundle b = new Bundle();
		intent = new Intent(HomeActivity.this, what.forum.ThreadActivity.class);
		b.putInt("id", subscriptions.getResponse().getThreads().get(i).getThreadId().intValue());
		b.putInt("postId", subscriptions.getResponse().getThreads().get(i).getLastPostId().intValue());
		intent.putExtras(b);
		startActivity(intent);
		scrollLayout.removeViewAt(i);
		threadList.remove(i);
		subscriptions.getResponse().getThreads().remove(i);
	}

	private void populateLayout() {
		if (subscriptions.hasUnreadThreads()) {
			List<Threads> threads = subscriptions.getResponse().getThreads();
			for (int i = 0; i < threads.size(); i++) {
				if ((i % 2) == 0) {
					threadList.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_odd, null));
				} else {
					threadList.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_even, null));
				}
				threadList.get(i).setTextSize(18);
				threadList.get(i).setText(threads.get(i).getForumName() + " > " + threads.get(i).getThreadTitle());
				threadList.get(i).setId(i);
				threadList.get(i).setOnClickListener(this);
				scrollLayout.addView(threadList.get(i));
			}
		}
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (threadList.size()); i++) {
			if (v.getId() == threadList.get(i).getId()) {
				openThread(i);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			super.onKeyDown(keyCode, event);
		}
		return false;
	}

	@Override
	public void onLeftGesturePerformed() {

	}

	private class LoadSubscriptions extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(HomeActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			subscriptions = Subscriptions.init();
			return subscriptions.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
				unlockScreenRotation();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(HomeActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
				unlockScreenRotation();
			}
		}
	}

}
