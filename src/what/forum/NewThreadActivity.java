package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.forum.section.Section;

public class NewThreadActivity extends MyActivity {
	private EditText threadTitle;
	private EditText threadBody;
	private int sectionId;

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

	public void post(View v) {
		String title = threadTitle.getText().toString();
		String body = threadBody.getText().toString();
		if ((title.length() > 1) && (body.length() > 1)) {
			Section.createNewThread(sectionId, title, body);
		} else {
			Toast.makeText(this, "Form not complete", Toast.LENGTH_LONG).show();
		}
	}

	public void cancel(View v) {
		finish();
	}
}