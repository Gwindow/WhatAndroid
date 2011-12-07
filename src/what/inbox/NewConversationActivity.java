package what.inbox;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.user.PrivateMessage;
import api.util.Tuple;

public class NewConversationActivity extends MyActivity {
	private EditText messageBody;
	private EditText messageSubject;
	private int userId;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.newmessage);

		getBundle();
		messageSubject = (EditText) this.findViewById(R.id.messageSubject);
		messageBody = (EditText) this.findViewById(R.id.messageBody);
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		userId = b.getInt("userId");
	}

	@SuppressWarnings("unchecked")
	public void send(View v) {
		String subject = messageSubject.getText().toString();
		String body = messageBody.getText().toString();
		if ((subject.length() > 1) && (body.length() > 1)) {
			new SendMessage().execute(new Tuple<String, String>(subject, body));
		} else {
			Toast.makeText(this, "Form not complete", Toast.LENGTH_LONG).show();
		}
	}

	public void cancel(View v) {
		finish();
	}

	private class SendMessage extends AsyncTask<Tuple<String, String>, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(NewConversationActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Tuple<String, String>... params) {
			try {
				PrivateMessage pm = new PrivateMessage(userId, params[0].getA(), params[0].getB());
				pm.sendMessage();
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
				Toast.makeText(NewConversationActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
			}
			if (status == false) {
				Toast.makeText(NewConversationActivity.this, "Could not send message", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();

			finish();
		}
	}

}