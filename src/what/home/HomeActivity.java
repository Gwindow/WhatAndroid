package what.home;

import java.util.List;

import what.forum.ThreadActivity;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.cli.Utils;
import api.index.Index;
import api.soup.MySoup;
import api.subscriptions.Subscriptions;
import api.subscriptions.Threads;
import api.util.Tuple;

/**
 * @author Gwindow
 * @since Jun 10, 2012 12:47:24 PM
 */
public class HomeActivity extends MyActivity2 implements OnClickListener {
	private LinearLayout scrollLayout;
	private TextView uploadView, downloadView, ratioView, bufferView;
	private TextView inboxView, notificationsView;

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
	}

	@Override
	public void prepare() {
		setActionBarTitle(MySoup.getUsername());

		new LoadInfo().execute();
		new LoadSubscriptions().execute();
	}

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
		}

		Number notifications = index.getResponse().getNotifications().getNotifications();
		if (notifications != null) {
			notificationsView.setText("Notifications: " + notifications);
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
	}

	public void openNotifications(View v) {
		startActivity(new Intent(this, what.notifications.NotificationsActivity.class));
	}

	private class LoadInfo extends AsyncTask<Void, Void, Boolean> {
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

	private class LoadSubscriptions extends AsyncTask<Void, Void, Boolean> {
		private ProgressBar bar;

		@Override
		protected void onPreExecute() {
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

}
