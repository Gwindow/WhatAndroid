package what.inbox;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewConversationActivity extends MyActivity {
	private EditText messageBody;
	private EditText messageSubject;
	private String userId;

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
		userId = b.getString("userId");
	}

	public void send(View v) {
		String subject = messageSubject.getText().toString();
		String body = messageBody.getText().toString();
		// TODO do the sending message code
	}

	public void cancel(View v) {
		finish();
	}
}