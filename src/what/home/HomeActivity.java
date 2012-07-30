package what.home;

import java.util.List;

import what.forum.ThreadActivity;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import what.search.TorrentSearchActivity;
import what.settings.Settings;
import what.user.UserActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.cli.Utils;
import api.index.Index;
import api.soup.MySoup;
import api.subscriptions.Subscriptions;
import api.subscriptions.Threads;
import api.util.Tuple;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 10, 2012 12:47:24 PM
 */
public class HomeActivity extends MyActivity2 implements OnClickListener {
	private LinearLayout scrollLayout;
	private TextView uploadView, downloadView, ratioView, bufferView;
	private TextView inboxView, notificationsView;

	private EditText searchView;

	private Index index;
	private Subscriptions subscriptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.HOME);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.home, false);
	}

	@Override
	public void init() {

	}

	@Override
	public void load() {
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		uploadView = (TextView) this.findViewById(R.id.upload);
		downloadView = (TextView) this.findViewById(R.id.download);
		ratioView = (TextView) this.findViewById(R.id.ratio);
		bufferView = (TextView) this.findViewById(R.id.buffer);
		inboxView = (TextView) this.findViewById(R.id.inbox);
		notificationsView = (TextView) this.findViewById(R.id.notifications);

		// searchView = (EditText) this.getLayoutInflater().inflate(R.layout.collapsible_edittext, null);
		// searchView.setOnEditorActionListener(this);
		// searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		((EditText) findViewById(R.layout.collapsible_edittext)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					Intent intent = new Intent(HomeActivity.this, TorrentSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(BundleKeys.SEARCH_STRING, view.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void prepare() {
		setActionBarTitle(MySoup.getUsername());

		new LoadInfo().execute();
		if (Settings.getSubscriptionsEnabled()) {
			new LoadSubscriptions().execute();
		} else {
			((TextView) this.findViewById(R.id.subscriptionsHeader)).setVisibility(View.GONE);
		}
	}

	// TODO caching
	private void populateInfo() {
		Number upload = index.getResponse().getUserstats().getUploaded();
		if (upload != null) {
			uploadView.setText("Up: " + Utils.toHumanReadableSize(upload.longValue()));
		}

		Number download = index.getResponse().getUserstats().getDownloaded();
		if (download != null) {
			downloadView.setText("Down: " + Utils.toHumanReadableSize(download.longValue()));
		}

		Number ratio = index.getResponse().getUserstats().getRatio();
		if (ratio != null) {
			ratioView.setText("Ratio: " + ratio);
		}

		Number buffer = index.getResponse().getUserstats().getBuffer();
		if (buffer != null) {
			bufferView.setText("Buffer: " + Utils.toHumanReadableSize(buffer.longValue()));
		}

		Number messages = index.getResponse().getNotifications().getMessages();
		if (messages != null) {
			inboxView.setText("Inbox: " + messages);
			if (messages.intValue() > 0) {
				inboxView.setTypeface(null, Typeface.BOLD);
			}
		}

		Number notifications = index.getResponse().getNotifications().getNotifications();
		if (notifications != null) {
			notificationsView.setText("Notifications: " + notifications);
			if (notifications.intValue() > 0) {
				notificationsView.setTypeface(null, Typeface.BOLD);
			}
		}
	}

	private void populateSubscriptions() {
		if (subscriptions.getResponse().getThreads() != null) {
			List<Threads> threads = subscriptions.getResponse().getThreads();

			for (int i = 0; i < threads.size(); i++) {
				TextView thread = (TextView) getLayoutInflater().inflate(R.layout.subscription_thread, null);
				thread.setText(threads.get(i).getThreadTitle());
				thread.setTag(new Tuple<Number, Number>(threads.get(i).getThreadId(), threads.get(i).getPostId()));
				thread.setId(i);
				thread.setOnClickListener(this);
				scrollLayout.addView(thread);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof Tuple<?, ?>) {
			Tuple<Number, Number> tuple = (Tuple<Number, Number>) v.getTag();
			openThread(tuple.getA(), tuple.getB());
			scrollLayout.removeViewAt(v.getId());
		}
	}

	private void openThread(Number threadId, Number lastReadPostId) {
		Intent intent = new Intent(this, ThreadActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.THREAD_ID, threadId.intValue());
		if (lastReadPostId != null) {
			bundle.putInt(BundleKeys.THREAD_LAST_READ_POST_ID, lastReadPostId.intValue());

		}
		intent.putExtras(bundle);
		startActivity(intent);

	}

	public void openInbox(View v) {
		startActivity(new Intent(this, what.inbox.InboxActivity.class));
		inboxView.setTypeface(null, Typeface.NORMAL);
		inboxView.setText("Inbox: 0");

	}

	public void openNotifications(View v) {
		startActivity(new Intent(this, what.notifications.NotificationsActivity.class));
		notificationsView.setTypeface(null, Typeface.NORMAL);
		notificationsView.setText("Notifications: 0");
	}

	public void refreshSubscriptions(View v) {
		try {
			scrollLayout.removeAllViews();
		} catch (Exception e) {
		}
		new LoadSubscriptions().execute();

	}

	@Override
	public void openHome(View v) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, MySoup.getUserId());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO figure out themes
		menu.add("Search").setIcon(R.drawable.ic_search_inverse).setActionView(R.layout.collapsible_edittext)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * @Override public boolean onEditorAction(TextView view, int actionId, KeyEvent event) { if (actionId ==
	 * EditorInfo.IME_ACTION_SEARCH) { Intent intent = new Intent(this, TorrentSearchActivity.class); Bundle bundle =
	 * new Bundle(); bundle.putString(BundleKeys.SEARCH_STRING, view.getText().toString()); intent.putExtras(bundle);
	 * startActivity(intent); return true; } return false; }
	 */

	private class LoadInfo extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		public LoadInfo() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			index = Index.init();
			return index.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status) {
				populateInfo();
			} else {
				ErrorToast.show(HomeActivity.this, HomeActivity.class);
			}
		}
	}

	private class LoadSubscriptions extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		private ProgressBar bar;

		public LoadSubscriptions() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			findViewById(R.id.subscriptionsHeader).setClickable(false);
			bar = new ProgressBar(HomeActivity.this);
			bar.setIndeterminate(true);
			scrollLayout.addView(bar);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			subscriptions = Subscriptions.init();
			return subscriptions.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			findViewById(R.id.subscriptionsHeader).setClickable(true);
			hideProgressBar();
			if (status) {
				populateSubscriptions();
			} else {
				ErrorToast.show(HomeActivity.this, HomeActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		super.onBackPressed();
	}

}
