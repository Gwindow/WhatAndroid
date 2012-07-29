package what.gui;

import what.forum.QuoteBuffer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.forum.thread.Thread;
import api.inbox.PrivateMessage;

/**
 * Activity that handles posting replys to threads or messages.
 * 
 * @author Gwindow
 * @since May 26, 2012 11:32:14 PM
 */
public class ReplyActivity extends MyActivity2 {
	private EditText body;
	private String type;
	private int id, userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Dialog);
		super.onCreate(savedInstanceState);
		enableFade();
		super.setContentView(R.layout.reply);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		type = bundle.getString(BundleKeys.REPLY_TYPE);

		if (type.equals(BundleKeys.REPLY_TYPE_THREAD)) {
			id = bundle.getInt(BundleKeys.THREAD_ID);
		}
		if (type.equals(BundleKeys.REPLY_TYPE_MESSAGE)) {
			id = bundle.getInt(BundleKeys.CONVERSATION_ID);
			userId = bundle.getInt(BundleKeys.USER_ID);
		}
		if (type.equals(BundleKeys.REPLY_TYPE_COMMENT)) {
			id = bundle.getInt(BundleKeys.GROUP_ID);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		body = (EditText) findViewById(R.id.body);
		body.setText(QuoteBuffer.getBuffer(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		fade();

		setActionBarTitle("Reply...");
	}

	public void reply(View v) {
		if (body.length() > 0) {
			new Post().execute(body.getText().toString());
			finish();
		} else {
			Toast.makeText(this, "Nothing entered", Toast.LENGTH_SHORT).show();
		}
	}

	private class Post extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean toReturn = false;
			if (type.equals(BundleKeys.REPLY_TYPE_THREAD)) {
				try {
					Thread.postReply(id, params[0]);
					toReturn = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (type.equals(BundleKeys.REPLY_TYPE_MESSAGE)) {
				try {
					// TODO why doesn't replying work?
					new PrivateMessage(userId, id, params[0]).replyMessage();
					toReturn = true;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			if (type.equals(BundleKeys.REPLY_TYPE_COMMENT)) {
				// TODO Do something.
			}
			return toReturn;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(ReplyActivity.this, "Could not post reply", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ReplyActivity.this, "Replied", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onPause() {
		enableFade();
		super.onPause();
	}

}
