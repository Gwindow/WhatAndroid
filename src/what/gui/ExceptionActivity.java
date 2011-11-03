package what.gui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Special dialog activity that shows a super secret message
 * 
 * @author Tim
 * 
 */
public class ExceptionActivity extends MyActivity {
	private Intent intent;
	private ImageView imageView;
	private TextView textView;
	private String message;
	private Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		@SuppressWarnings("unused")
		ReportSender reportSender = new ReportSender(this);
		setContentView(R.layout.dave);

		imageView = (ImageView) this.findViewById(R.id.imageView1);
		textView = (TextView) this.findViewById(R.id.textView1);
		getBundle();
		textView.setText(message);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				finish();
			}

		}, 5000);
		;
	}

	public void getBundle() {
		Bundle b = this.getIntent().getExtras();
		message = b.getString("message");
	}
}
