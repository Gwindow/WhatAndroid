package what.inbox;

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
import what.gui.ViewSlider;
import what.settings.Settings;
import what.user.UserActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.inbox.inbox.Inbox;
import api.inbox.inbox.Messages;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since May 25, 2012 5:08:29 PM
 */
public class InboxActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int MESSAGE_TAG = 0;
	private static final int SENDER_TAG = 1;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private Inbox inbox;
	private int inboxPage = 1;

	private boolean isLoaded;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.INBOX);
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
			inboxPage = bundle.getInt(BundleKeys.INBOX_PAGE);
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
	 * Populate inbox with messages.
	 */
	private void populate() {
		setActionBarTitle("Inbox, " + inboxPage + "/" + inbox.getResponse().getPages());

		List<Messages> messages = inbox.getResponse().getMessages();

		if (messages != null) {
			for (int i = 0; i < messages.size(); i++) {
				ViewSlider message_layout = (ViewSlider) getLayoutInflater().inflate(R.layout.inbox_message, null);

				TextView message_title = (TextView) message_layout.findViewById(R.id.messageTitle);
				message_title.setText(messages.get(i).getSubject());
				message_title.setId(messages.get(i).getConvId().intValue());
				message_title.setTag(MESSAGE_TAG);
				message_title.setOnClickListener(this);

				TextView sender = (TextView) message_layout.findViewById(R.id.messageSender);
				sender.setText("Sender: " + messages.get(i).getUsername());
				if (!messages.get(i).isSystem()) {
					sender.setId(messages.get(i).getSenderId().intValue());
					sender.setTag(SENDER_TAG);
					sender.setOnClickListener(this);
				}
				TextView date = (TextView) message_layout.findViewById(R.id.messageDate);
				date.setText("Date: " + messages.get(i).getDate());

				scrollLayout.addView(message_layout);
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
			if (inboxPage < inbox.getLastPage()) {
				inboxPage++;
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
			case MESSAGE_TAG:
				openMessage(v.getId());
				break;
			case SENDER_TAG:
				openUser(v.getId());
				break;
			default:
				break;
		}
	}

	private void openMessage(int id) {
		Intent intent = new Intent(this, ConversationActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.CONVERSATION_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	private void openUser(int id) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void jumpToPage() {
		new JumpToPageDialog(this, inbox.getResponse().getPages()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(InboxActivity.this, InboxActivity.class);
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
		inflater.inflate(R.menu.inbox_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.refresh_item:
				refresh();
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
				bar = new ProgressBar(InboxActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();

			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			inbox = Inbox.inboxFromPage(inboxPage);
			return inbox.getStatus();
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
				ErrorToast.show(InboxActivity.this, InboxActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}
}
