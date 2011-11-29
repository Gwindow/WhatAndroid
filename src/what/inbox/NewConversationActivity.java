package what.inbox;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.user.PrivateMessage;

public class NewConversationActivity extends MyActivity {
	private EditText messageBody;
	private EditText messageSubject;
	private int userId;

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

	public void send(View v) {
		String subject = messageSubject.getText().toString();
		String body = messageBody.getText().toString();
		if ((subject.length() > 1) && (body.length() > 1)) {
			PrivateMessage pm = new PrivateMessage(userId, subject, body);
			pm.sendMessage();
		} else {
			Toast.makeText(this, "Form not complete", Toast.LENGTH_LONG).show();
		}
	}

	public void cancel(View v) {
		finish();
	}

}