package what.inbox;

import java.util.ArrayList;
import java.util.List;

import what.forum.QuoteBuffer;
import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.inbox.conversation.Conversation;
import api.inbox.conversation.Messages;

public class ConversationActivity extends MyActivity implements OnLongClickListener {
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private Conversation conversation;
	private Intent intent;
	private int id;
	private TextView conversationTitle;
	private ArrayList<RelativeLayout> listOfMessages = new ArrayList<RelativeLayout>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);

		conversationTitle = (TextView) findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		getBundle();

		new LoadThread().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
	}

	private void populateLayout() {
		conversationTitle.setText(conversation.getResponse().getSubject());
		List<Messages> messages = conversation.getResponse().getMessages();
		RelativeLayout layout;
		TextView username;
		WebView body;
		for (int i = 0; i < messages.size(); i++) {
			layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.message, null);
			username = (TextView) layout.findViewById(R.id.username);
			username.setText(messages.get(i).getSenderName());
			body = (WebView) layout.findViewById(R.id.post);
			body.loadData(messages.get(i).getBody(), "text/html", "utf-8");
			// body.setBackgroundColor(R.drawable.btn_black);
			listOfMessages.add(layout);
			listOfMessages.get(i).setId(i);
			listOfMessages.get(i).setClickable(true);
			listOfMessages.get(i).setOnLongClickListener(this);
			scrollLayout.addView(listOfMessages.get(i));
		}
	}

	private void openOptions(int i) {
		Bundle b = new Bundle();
		intent = new Intent(ConversationActivity.this, what.inbox.MessageOptionsActivity.class);
		b.putString("post", conversation.getResponse().getMessages().get(i).getQuotableBody());
		b.putInt("userId", conversation.getResponse().getMessages().get(i).getSenderId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void back(View v) {
		finish();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				back(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		for (int i = 0; i < (listOfMessages.size()); i++) {
			if (v.getId() == listOfMessages.get(i).getId()) {
				openOptions(i);
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		QuoteBuffer.clear();
		super.onDestroy();
	}

	private class LoadThread extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ConversationActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			conversation = Conversation.conversationFromId(id);
			return conversation.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(ConversationActivity.this, "Could not load conversation", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
