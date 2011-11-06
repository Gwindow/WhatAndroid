package what.inbox;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import api.soup.MySoup;

public class BugReportActivity extends MyActivity {
	private EditText messageBody;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bugreport);
		messageBody = (EditText) this.findViewById(R.id.messageBody);
	}

	public void send(View v) {
		String subject = "Bug Report from: " + MySoup.getUsername();
		String body = messageBody.getText().toString();
		// TODO do the sending message code
	}

	public void cancel(View v) {
		finish();
	}
}
