package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewThreadActivity extends MyActivity {
	private EditText threadTitle;
	private EditText threadBody;
	private String sectionId;

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
		sectionId = b.getString("sectionId");
	}

	public void post(View v) {
		String title = threadTitle.getText().toString();
		String body = threadBody.getText().toString();
		// TODO do the posting code
	}

	public void cancel(View v) {
		finish();
	}
}