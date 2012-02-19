package what.inbox;

import java.util.ArrayList;
import java.util.List;

import what.forum.QuoteBuffer;
import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.inbox.conversation.Conversation;
import api.inbox.conversation.Messages;
import api.user.PrivateMessage;

public class ConversationActivity extends MyActivity implements OnLongClickListener {
	private ScrollView scrollView;
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private Conversation conversation;
	private Intent intent;
	private int userId, convId;
	private TextView conversationTitle;
	private ArrayList<RelativeLayout> listOfMessages = new ArrayList<RelativeLayout>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.conversation, true);

		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		conversationTitle = (TextView) findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		getBundle();

		new LoadConversation().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		userId = b.getInt("id");
	}

	private void populateLayout() {
		convId = conversation.getResponse().getConvId().intValue();
		conversationTitle.setText(conversation.getResponse().getSubject());
		List<Messages> messages = conversation.getResponse().getMessages();
		RelativeLayout layout;
		TextView username;
		WebView body;
		for (int i = 0; i < messages.size(); i++) {
			layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.message, null);
			RelativeLayout relativeLayout = (RelativeLayout) layout.findViewById(R.id.content);

			username = (TextView) layout.findViewById(R.id.username);
			username.setText(messages.get(i).getSenderName());
			body = (WebView) layout.findViewById(R.id.body);
			body.loadData(messages.get(i).getBody().trim(), "text/html", "utf-8");

			body.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
			body.getSettings().setSupportZoom(true);
			body.setVerticalScrollBarEnabled(true);
			body.setVerticalScrollbarOverlay(true);

			if ((i % 2) == 0) {
				relativeLayout.setBackgroundResource(R.drawable.color_transparent_white);
				body.setBackgroundColor(0);
			} else {
				relativeLayout.setBackgroundResource(R.drawable.color_transparent_light_gray);
				body.setBackgroundColor(0);
			}

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
		b.putInt("convId", conversation.getResponse().getConvId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void onDownGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);

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

	private class LoadConversation extends AsyncTask<Void, Void, Boolean> {
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
			conversation = Conversation.conversationFromId(userId);
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

	public void reply(View v) {
		replyDialog();
	}

	private void replyDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("Reply");

		final EditText input = new EditText(this);
		input.setGravity(Gravity.TOP);
		input.setGravity(Gravity.LEFT);
		input.setMinHeight(this.getHeight() / 3);
		input.setMinWidth(this.getWidth() / 2);
		input.setText(QuoteBuffer.getBuffer());
		alert.setView(input);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (input.getText().length() > 0) {
					new PostReply().execute(input.getText().toString());
				} else {
					Toast.makeText(ConversationActivity.this, "Enter a reply", Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
	}

	private class PostReply extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ConversationActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Sending...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				PrivateMessage pm = new PrivateMessage(userId, convId, params[0]);
				pm.replyMessage();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				Toast.makeText(ConversationActivity.this, "Reply sent", Toast.LENGTH_SHORT).show();
			}
			if (status == false) {
				Toast.makeText(ConversationActivity.this, "Could not send reply", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
