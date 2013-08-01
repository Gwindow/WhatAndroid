package what.status;

import api.whatstatus.Status;
import what.gui.ActivityNames;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import api.whatstatus.WhatStatus;

public class WhatStatusActivity extends MyActivity2 {
	private ImageView siteStatus, trackerStatus, ircStatus;
	private WhatStatus whatStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.STATUS);
		super.requestIndeterminateProgress();
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.whatstatus);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		siteStatus = (ImageView) this.findViewById(R.id.sitestatus);
		trackerStatus = (ImageView) this.findViewById(R.id.trackerstatus);
		ircStatus = (ImageView) this.findViewById(R.id.ircstatus);
	}

	@Override
	public void prepare() {
		setActionBarTitle("WhatStatus");
		new Load().execute();
	}

	public void populateLayout() {
		//Having some issues with getting the whatstatus site information for some reason (SSL errors??)
		//so do a null check for now so we don't crash
		if (whatStatus == null || whatStatus.getStatus() == null)
			return;

		Status status = whatStatus.getStatus();
		if (status.siteUp())
			siteStatus.setImageResource(R.drawable.site_up);
		else
			siteStatus.setImageResource(R.drawable.site_down);
		if (status.trackerUp())
			trackerStatus.setImageResource(R.drawable.site_up);
		else
			trackerStatus.setImageResource(R.drawable.site_down);
		if (status.ircUp())
			ircStatus.setImageResource(R.drawable.site_up);
		else
			ircStatus.setImageResource(R.drawable.site_down);

	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		public Load() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				whatStatus = WhatStatus.init();
				// TODO always returns true until a way to get request status can be implement into whatstatus.info
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			//TODO Is there a need for this ordering? couldn't it just be an if/else
			//and put unlockScreenRotation before the if?
			hideIndeterminateProgress();
			unlockScreenRotation();
			if (status)
				populateLayout();
			else
				ErrorToast.show(WhatStatusActivity.this, WhatStatusActivity.class);
		}

	}
}
