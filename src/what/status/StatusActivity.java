package what.status;

import what.gui.MyActivity;
import what.gui.R;
import what.gui.ReportSender;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import api.whatstatus.WhatStatus;

public class StatusActivity extends MyActivity {
	// LinearLayout twitterScroll;
	ImageView siteStatus, trackerStatus, ircStatus;
	// ArrayList<TextView> tweetList = new ArrayList<TextView>();
	WhatStatus whatStatus;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.whatstatus);
		// twitterScroll = (LinearLayout) this.findViewById(R.id.twitterScroll);
		// twitterScroll.setOrientation(LinearLayout.VERTICAL);
		siteStatus = (ImageView) this.findViewById(R.id.sitestatus);
		trackerStatus = (ImageView) this.findViewById(R.id.trackerstatus);
		ircStatus = (ImageView) this.findViewById(R.id.ircstatus);

		new LoadStatus().execute();
	}

	public void populateLayout() {
		switch (whatStatus.getStatus().getSite()) {
		case 0:
			siteStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		case 1:
			siteStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_down));
			break;
		case 2:
			siteStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_maintenance));
			break;
		default:
			siteStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		}
		switch (whatStatus.getStatus().getTracker()) {
		case 0:
			trackerStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		case 1:
			trackerStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_down));
			break;
		case 2:
			trackerStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_maintenance));
			break;
		default:
			trackerStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		}
		switch (whatStatus.getStatus().getIrc()) {
		case 0:
			ircStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		case 1:
			ircStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		}
		/* try { for (int i = 0; i < status.getTweets().size(); i++) { tweetList.add(new TextView(this));
		 * tweetList.get(i).setTextSize(18); tweetList.get(i).setText(status.getTweets().get(i).getA() + "\n \t" +
		 * status.getTweets().get(i).getB()); twitterScroll.addView(tweetList.get(i)); } } catch (TwitterException e) {
		 * Toast.makeText(this, "Could not load tweets", Toast.LENGTH_LONG).show(); e.printStackTrace(); } */
	}

	public void openTwitter(View v) {
		String url = "http://twitter.com/#!/whatcd";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	public void openWhatStatus(View v) {
		String url = "http://whatstatus.info/";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private class LoadStatus extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(StatusActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			whatStatus = WhatStatus.init();
			// always returns true until a way to get request status can be implement into whatstatus.info
			return true;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(StatusActivity.this, "Could not load whatstatus", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
