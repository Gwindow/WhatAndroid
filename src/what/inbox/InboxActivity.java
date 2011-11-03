/**
 * 
 */
package what.inbox;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import what.services.InboxService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.inbox.inbox.Inbox;
import api.inbox.inbox.Messages;

public class InboxActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> messageList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Intent intent;
	private ProgressDialog dialog;
	private int counter;
	private List<Messages> messages;
	private NotificationManager myNotificationManager;
	private Inbox inbox;
	private Button backButton, nextButton;
	private int page;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox);
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		myNotificationManager.cancel(InboxService.ID);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);

		getBundle();

		if (page == 1) {
			if (InboxService.isRunning()) {
				inbox = InboxService.inbox;
				populateLayout();
			}
			if (!InboxService.isRunning()) {
				new LoadInbox().execute();
			}
		} else {
			new LoadInbox().execute();
		}
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		try {
			page = b.getInt("page");
		} catch (Exception e) {
			page = 1;
		}
	}

	public void next(View v) {
		Bundle b = new Bundle();
		intent = new Intent(InboxActivity.this, what.inbox.InboxActivity.class);
		b.putInt("page", page + 1);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void back(View v) {
		Bundle b = new Bundle();
		intent = new Intent(InboxActivity.this, what.inbox.InboxActivity.class);
		b.putInt("page", page - 1);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void populateLayout() {
		backButton.setEnabled(inbox.hasPreviousPage());
		nextButton.setEnabled(inbox.hasNextPage());

		messages = inbox.getResponse().getMessages();
		for (int i = 0; i < messages.size(); i++) {
			if ((i % 2) == 0) {
				messageList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				messageList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}

			messageList.get(i).setText("Subject: " + messages.get(i).getSubject() + "\nSender: " + messages.get(i).getUsername());
			messageList.get(i).setId(i);
			messageList.get(i).setOnClickListener(this);
			if (messages.get(i).isUnread()) {
				messageList.get(i).setTextColor(Color.RED);
				messageList.get(i).setTextSize(17);
			}
			scrollLayout.addView(messageList.get(i));
			counter++;
		}
	}

	private void openMessage(int i) {
		// TODO fill out
	}

	@Override
	public void onClick(View v) {
		if ((v.getId() >= 0) && (counter >= v.getId())) {
			openMessage(v.getId());
		}
	}

	private class LoadInbox extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(InboxActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			inbox = Inbox.inboxFromPage(page);
			return inbox.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			if (status == false) {
				Toast.makeText(InboxActivity.this, "Could not load inbox", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}