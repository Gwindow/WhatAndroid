package what.inbox;

import java.util.List;

import what.forum.QuoteBuffer;
import what.gui.ActivityNames;
import what.gui.AsyncImageGetter;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.ReplyActivity;
import what.user.UserActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
	private MyScrollView scrollView;

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
		Bundle bundle = getIntent().getExtras();
		conversationId = bundle.getInt(BundleKeys.CONVERSATION_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		scrollView = (MyScrollView) this.findViewById(R.id.scrollView);
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
			int width = getMetrics().widthPixels;
			int height = getMetrics().heightPixels;
			for (int i = 0; i < messages.size(); i++) {
				LinearLayout message_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.conversation_message, null);

				TextView author = (TextView) message_layout.findViewById(R.id.author);
				author.setText(messages.get(i).getSenderName());

				TextView date = (TextView) message_layout.findViewById(R.id.date);
				date.setText(messages.get(i).getSentDate());

				TextView body = (TextView) message_layout.findViewById(R.id.body);
				body.setText(Html.fromHtml(messages.get(i).getBody(), new AsyncImageGetter(body, this, width, height), null));
				Linkify.addLinks(body, Linkify.WEB_URLS);

				ImageView reply = (ImageView) message_layout.findViewById(R.id.replyIcon);
				reply.setTag(REPLY_TAG);
				reply.setId(i);
				reply.setOnClickListener(this);

				ImageView quote = (ImageView) message_layout.findViewById(R.id.quoteIcon);
				quote.setTag(QUOTE_TAG);
				quote.setId(i);
				quote.setOnClickListener(this);

				ImageView user = (ImageView) message_layout.findViewById(R.id.userIcon);
				if (!messages.get(i).isSystem()) {
					user.setTag(USER_TAG);
					user.setId(i);
					user.setOnClickListener(this);
				}
				scrollLayout.addView(message_layout);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REPLY_TAG:
				if (!conversation.getResponse().getMessages().get(v.getId()).isSystem()) {
					QuoteBuffer.add(conversationId, (conversation.getResponse().getMessages().get(v.getId()).getQuotableBody()));
					reply();
				}
				break;
			case QUOTE_TAG:
				QuoteBuffer.add(conversationId, (conversation.getResponse().getMessages().get(v.getId()).getQuotableBody()));
				Toast.makeText(this, "Quoted", Toast.LENGTH_SHORT).show();
				break;
			case USER_TAG:
				if (!conversation.getResponse().getMessages().get(v.getId()).isSystem()) {
					openUser(conversation.getResponse().getMessages().get(v.getId()).getSenderId().intValue());
				}
				break;
			case android.R.id.home:
				homeIconJump(scrollView);
				break;
			default:
				break;
		}
	}

	private void openUser(int id) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
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
		public Load() {
			super();
		}

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			conversation = Conversation.conversationFromId(conversationId);
			return conversation.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(ConversationActivity.this, ConversationActivity.class);
		}
	}

}
