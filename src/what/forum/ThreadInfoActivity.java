package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

//TODO enable thread subscribed in php
public class ThreadInfoActivity extends MyActivity implements OnCheckedChangeListener {
	private CheckBox subscribed;
	private boolean isSubscribed;
	private int id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.threadinfo);

		getBundle();

		subscribed = (CheckBox) this.findViewById(R.id.subscribed);
		subscribed.setOnCheckedChangeListener(this);
		subscribed.setChecked(isSubscribed);

	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		isSubscribed = b.getBoolean("subscribed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
	 * boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if (v.getId() == subscribed.getId()) {
			if (isChecked) {
			}
		}
	}
}
