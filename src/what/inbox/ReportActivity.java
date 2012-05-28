package what.inbox;

import what.gui.ErrorReporter;
import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import api.inbox.PrivateMessage;

//TODO MODIFY
public class ReportActivity extends MyActivity {
	private static final String SUBJECT_ONE = "Bug Report";
	private static final String SUBJECT_TWO = "Suggestion Report";
	private static final String SUBJECT_THREE = "Bug/Suggestion Report";

	private EditText messageBody;
	private String subject;
	private String body;
	private ErrorReporter errorReporter;
	private String report;
	private CheckBox bugCheckBox, suggestionCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.report, true);
	}

	@Override
	public void init() {
		errorReporter = new ErrorReporter();
		errorReporter.init(this);
		report = "[hide]" + errorReporter.CreateInformationString() + "[/hide]";
	}

	@Override
	public void load() {
		bugCheckBox = (CheckBox) this.findViewById(R.id.bugCheckBox);
		suggestionCheckBox = (CheckBox) this.findViewById(R.id.suggestionCheckBox);
		messageBody = (EditText) this.findViewById(R.id.messageBody);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	public void send(View v) {
		body = messageBody.getText().toString();
		if (bugCheckBox.isChecked() || suggestionCheckBox.isChecked()) {
			subject = getSubject();
			if (body.length() > 0) {
				body += "\n\n" + report;
				new SendMessage().execute();
			} else {
				Toast.makeText(this, "Please fill out form", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "Please select at least one checkbox", Toast.LENGTH_LONG).show();
		}
	}

	private String getSubject() {
		String s = "";
		if (bugCheckBox.isChecked() && !suggestionCheckBox.isChecked()) {
			s = SUBJECT_ONE;
		}
		if (!bugCheckBox.isChecked() && suggestionCheckBox.isChecked()) {
			s = SUBJECT_TWO;
		}
		if (bugCheckBox.isChecked() && suggestionCheckBox.isChecked()) {
			s = SUBJECT_THREE;
		}
		return s;
	}

	public void cancel(View v) {
		finish();
	}

	private class SendMessage extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ReportActivity.this);
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
				Toast.makeText(ReportActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(ReportActivity.this, "Could not send mesasge", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
