package what.inbox;

import java.util.Calendar;

import what.gui.ErrorReporter;
import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.inbox.PrivateMessage;

public class BugReportActivity extends MyActivity {
	private EditText messageBody;
	private String subject;
	private String body;
	private ErrorReporter errorReporter;
	private String report;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bugreport, true);
		errorReporter = new ErrorReporter();
		errorReporter.init(this);
		report = "[hide]" + errorReporter.CreateInformationString() + "[/hide]";
		messageBody = (EditText) this.findViewById(R.id.messageBody);
	}

	public void send(View v) {
		Calendar calendar = Calendar.getInstance();
		subject = "Bug Report, " + calendar.get(Calendar.DATE);
		body = messageBody.getText().toString();
		if (body.length() > 0) {
			body += "\n" + report;
			new SendMessage().execute();
		} else {
			Toast.makeText(this, "Please fill out form", Toast.LENGTH_LONG).show();
		}
	}

	public void cancel(View v) {
		finish();
	}

	private class SendMessage extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(BugReportActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Sending...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				PrivateMessage pm = new PrivateMessage(135837, subject, body);
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
				Toast.makeText(BugReportActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(BugReportActivity.this, "Could not send mesasge", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
