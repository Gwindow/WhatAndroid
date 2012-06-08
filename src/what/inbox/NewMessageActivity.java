package what.inbox;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import api.inbox.PrivateMessage;

/**
 * @author Gwindow
 * @since Jun 5, 2012 10:07:21 PM
 */
public class NewMessageActivity extends MyActivity2 {
	private EditText subject;
	private EditText body;
	private int userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Dialog);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.setContentView(R.layout.new_message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		userId = bundle.getInt(BundleKeys.USER_ID);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		subject = (EditText) findViewById(R.id.subject);
		body = (EditText) findViewById(R.id.body);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		setActionBarTitle("Compose...");
	}

	public void send(View v) {
		if (body.length() > 0) {
			new Send().execute(subject.getText().toString(), body.getText().toString());
			finish();
		} else {
			Toast.makeText(this, "Nothing entered", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onPause();
	}

	private class Send extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean toReturn = false;
			try {
				String subject = params[0].length() == 0 ? "No subject" : params[0];
				new PrivateMessage(userId, subject, params[1]).sendMessage();
				toReturn = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return toReturn;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(NewMessageActivity.this, "Could not send message", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NewMessageActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

			}
		}

	}

}
