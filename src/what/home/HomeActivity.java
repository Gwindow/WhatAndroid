package what.home;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import what.settings.SettingsActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import api.subscriptions.Subscriptions;
import api.subscriptions.Threads;

public class HomeActivity extends MyActivity implements OnClickListener {
	private Subscriptions subscriptions;
	private TextView username, uploadedValue, downloadedValue, ratioValue, bufferValue;
	private EditText searchBar;
	private String searchTerm;
	private ArrayList<TextView> threadList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private Intent intent;
	private DecimalFormat df = new DecimalFormat("#.00");
	private int counter = 0;
	private Intent inboxService;
	private Intent notificationService;
	private Intent annoucementService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.home);
		findViewById(R.id.home_root).setBackgroundResource(SettingsActivity.backgroundFromPreference(this));
		username = (TextView) this.findViewById(R.id.username);
		uploadedValue = (TextView) this.findViewById(R.id.upvalue);
		downloadedValue = (TextView) this.findViewById(R.id.downvalue);
		ratioValue = (TextView) this.findViewById(R.id.ratiovalue);
		bufferValue = (TextView) this.findViewById(R.id.buffervalue);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		searchBar = (EditText) this.findViewById(R.id.searchBar);
		// if (!Settings.getQuickSearch()) {
		// searchBar.setVisibility(EditText.INVISIBLE);
		// }

		username.setText(MySoup.getUsername());
		uploadedValue.setText("U: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getUploaded()) + "GB");
		downloadedValue.setText("D: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getDownloaded()) + "GB");
		ratioValue.setText("R: " + MySoup.getIndex().getResponse().getUserstats().getRatio());
		bufferValue.setText("B: " + toGBString(MySoup.getIndex().getResponse().getUserstats().getBuffer()) + "GB");
		new LoadSubscriptions().execute();

		// inboxService = new Intent(this, InboxService.class);
		// startService(inboxService);

		// notificationService = new Intent(this, NotificationService.class);
		// startService(notificationService);

		// annoucementService = new Intent(this, AnnouncementService.class);
		// startService(annoucementService);
	}

	private String toGBString(String s) {
		double d = Double.parseDouble(s) / Math.pow(1024, 3);
		return df.format(d);
	}

	public void openProfile(View v) {
		Bundle b = new Bundle();
		intent = new Intent(HomeActivity.this, what.user.UserProfileActivity.class);
		b.putInt("userId", Integer.valueOf(MySoup.getUserId()));
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void openTorrents(View v) {
		Intent intent = new Intent(this, what.gui.MainMenu.class);
		startActivityForResult(intent, 0);
	}

	public void openForum(View v) {
		intent = new Intent(HomeActivity.this, what.forum.ForumSectionsListActivity.class);
		startActivityForResult(intent, 0);
	}

	public void openInbox(View v) {
		intent = new Intent(HomeActivity.this, what.inbox.InboxActivity.class);
		startActivityForResult(intent, 0);
	}

	public void openTopTen(View v) {
		intent = new Intent(HomeActivity.this, what.top.TopTorrentsActivity.class);
		startActivityForResult(intent, 0);
	}

	private void openThread(int i) {
		Bundle b = new Bundle();
		intent = new Intent(HomeActivity.this, what.forum.ThreadActivity.class);
		b.putInt("id", subscriptions.getResponse().getThreads().get(i).getThreadId());
		b.putInt("page", (subscriptions.getResponse().getThreads().get(i).getLastReadPage()));
		intent.putExtras(b);
		startActivityForResult(intent, 0);
		scrollLayout.removeViewAt(i);
		threadList.remove(i);
	}

	private void populateLayout() {
		List<Threads> threads = subscriptions.getResponse().getThreads();
		for (int i = 0; i < threads.size(); i++) {
			if ((i % 2) == 0) {
				threadList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				threadList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			threadList.get(i).setTextSize(18);
			threadList.get(i).setText(threads.get(i).getForumName() + " > " + threads.get(i).getThreadTitle());
			threadList.get(i).setId(i);
			threadList.get(i).setOnClickListener(this);
			scrollLayout.addView(threadList.get(i));
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
			try {
				subscriptions = Subscriptions.init();
				return subscriptions.getStatus();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(HomeActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

}
