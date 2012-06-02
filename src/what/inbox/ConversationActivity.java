package what.inbox;

import java.util.List;

import what.forum.QuoteBuffer;
import what.gui.ActivityNames;
import what.gui.AsyncImageGetter;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import what.gui.ReplyActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.inbox.conversation.Conversation;
import api.inbox.conversation.Messages;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Displays the "conversation" inside a private message.
 * 
 * @author Gwindow
 * @since May 26, 2012 9:50:18 AM
 */
public class ConversationActivity extends MyActivity2 implements OnClickListener {
	private static final int REPLY_TAG = 0;
	private static final int QUOTE_TAG = 1;
	private static final int USER_TAG = 2;

	private LinearLayout scrollLayout;

	private Conversation conversation;
	private int conversationId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.INBOX);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.generic_scrollview, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		conversationId = bundle.getInt(BundleKeys.CONVERSATION_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
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
	 * Populate with messages.
	 */
	private void populate() {
		setActionBarTitle(conversation.getResponse().getSubject());

		List<Messages> messages = conversation.getResponse().getMessages();

		if (messages != null) {
			for (int i = 0; i < messages.size(); i++) {
				LinearLayout message_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.conversation_message, null);

				TextView author = (TextView) message_layout.findViewById(R.id.author);
				author.setText(messages.get(i).getSenderName());

				TextView date = (TextView) message_layout.findViewById(R.id.date);
				date.setText(messages.get(i).getSentDate());

				TextView body = (TextView) message_layout.findViewById(R.id.body);
				body.setText(Html.fromHtml(messages.get(i).getBody(), new AsyncImageGetter(body, this), null));

				ImageView reply = (ImageView) message_layout.findViewById(R.id.replyIcon);
				reply.setTag(REPLY_TAG);
				reply.setId(i);
				reply.setOnClickListener(this);

				ImageView quote = (ImageView) message_layout.findViewById(R.id.quoteIcon);
				quote.setTag(QUOTE_TAG);
				quote.setId(i);
				quote.setOnClickListener(this);

				ImageView user = (ImageView) message_layout.findViewById(R.id.userIcon);
				user.setTag(USER_TAG);
				user.setId(i);
				user.setOnClickListener(this);
				scrollLayout.addView(message_layout);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REPLY_TAG:
				QuoteBuffer.add(conversationId, Html.fromHtml(conversation.getResponse().getMessages().get(v.getId()).getBody())
						.toString());
				reply();
				break;
			case QUOTE_TAG:
				QuoteBuffer.add(conversationId, Html.fromHtml(conversation.getResponse().getMessages().get(v.getId()).getBody())
						.toString());
				break;
			case USER_TAG:
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.conversation_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reply_item:
				// close options menu for the fade effect
				closeOptionsMenu();
				reply();
				break;
			case R.id.refresh_item:
				refresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void reply() {
		Intent intent = new Intent(this, ReplyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.REPLY_TYPE, BundleKeys.REPLY_TYPE_MESSAGE);
		bundle.putInt(BundleKeys.CONVERSATION_ID, conversationId);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;

		public Load() {
			super();
		}

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
			conversation = Conversation.conversationFromId(conversationId);
			return conversation.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(ConversationActivity.this, ConversationActivity.class);
		}
	}

}
