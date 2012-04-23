package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ThreadInfoActivity extends MyActivity implements OnCheckedChangeListener {
	private static final int RESULT_UNSUBSCRIBE = 1;
	private static final int RESULT_SUBSCRIBE = 2;
	private static final int RESULT_REFRESH = 3;
	private CheckBox subscribed;
	private boolean isSubscribed;
	private int id;
	private int resultCode = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.threadinfo);

		getBundle();

		subscribed = (CheckBox) this.findViewById(R.id.subscribed);
		subscribed.setChecked(isSubscribed);
		subscribed.setOnCheckedChangeListener(this);

	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		isSubscribed = b.getBoolean("subscribed");
	}

	public void refresh(View v) {
		resultCode = RESULT_SUBSCRIBE;
		sendResult();
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if (v.getId() == subscribed.getId()) {
			if (isChecked) {
				resultCode = RESULT_SUBSCRIBE;
				sendResult();

			}
			if (!isChecked) {
				resultCode = RESULT_UNSUBSCRIBE;
				sendResult();
			}
		}
	}

	private void sendResult() {
		this.setResult(resultCode);
		finish();
	}

}