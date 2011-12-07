package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.forum.section.Section;
import api.util.Tuple;

public class NewThreadActivity extends MyActivity {
	private EditText threadTitle;
	private EditText threadBody;
	private int sectionId;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.newthread);

		getBundle();
		threadTitle = (EditText) this.findViewById(R.id.threadTitle);
		threadBody = (EditText) this.findViewById(R.id.threadBody);
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		sectionId = b.getInt("sectionId");
	}

	@SuppressWarnings("unchecked")
	public void post(View v) {
		String title = threadTitle.getText().toString();
		String body = threadBody.getText().toString();
		if ((title.length() > 1) && (body.length() > 1)) {
			new CreateNewThread().execute(new Tuple<String, String>(title, body));
		} else {
			Toast.makeText(this, "Form not complete", Toast.LENGTH_LONG).show();
		}
	}

	public void cancel(View v) {
		finish();
	}

	private class CreateNewThread extends AsyncTask<Tuple<String, String>, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(NewThreadActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Tuple<String, String>... params) {
			try {
				Section.createNewThread(sectionId, params[0].getA(), params[0].getB());
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
				Toast.makeText(NewThreadActivity.this, "New thread created", Toast.LENGTH_SHORT).show();
			}
			if (status == false) {
				Toast.makeText(NewThreadActivity.this, "Could not create thread", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
			finish();
		}
	}
}