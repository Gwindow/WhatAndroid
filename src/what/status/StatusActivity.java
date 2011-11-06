package what.status;

import java.util.ArrayList;

import twitter4j.TwitterException;
import what.gui.MyActivity;
import what.gui.R;
import what.gui.ReportSender;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.util.CouldNotLoadException;
import api.whatstatus.Status2;

public class StatusActivity extends MyActivity {
	LinearLayout twitterScroll;
	ImageView siteStatus, trackerStatus, ircStatus;
	ArrayList<TextView> tweetList = new ArrayList<TextView>();
	Status2 status;
	ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.whatstatus);
		twitterScroll = (LinearLayout) this.findViewById(R.id.twitterScroll);
		twitterScroll.setOrientation(LinearLayout.VERTICAL);
		siteStatus = (ImageView) this.findViewById(R.id.sitestatus);
		trackerStatus = (ImageView) this.findViewById(R.id.trackerstatus);
		ircStatus = (ImageView) this.findViewById(R.id.ircstatus);

		progress = new ProgressDialog(this);
		progress.setIndeterminate(true);
		progress.setMessage("Loading whatstatus...");
		progress.show();

		Thread loadingThread = new Thread() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							StatusActivity.this.lockScreenRotation();
							status = new Status2();
							loadingHandler.sendEmptyMessage(0);
						} catch (CouldNotLoadException e) {
							Toast.makeText(StatusActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							progress.dismiss();
							StatusActivity.this.unlockScreenRotation();
							finish();
						}
					}
				});
			}

			Handler loadingHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					populateLayout();
					progress.dismiss();
					StatusActivity.this.unlockScreenRotation();
				}
			};
		};
		loadingThread.start();

	}

	public void populateLayout() {
		switch (status.getSiteStatus()) {
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
		switch (status.getTrackerStatus()) {
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
		switch (status.getIrcStatus()) {
		case 0:
			ircStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		case 1:
			ircStatus.setImageDrawable(this.getResources().getDrawable(R.drawable.site_up));
			break;
		}
		try {
			for (int i = 0; i < status.getTweets().size(); i++) {
				tweetList.add(new TextView(this));
				tweetList.get(i).setTextSize(18);
				tweetList.get(i).setText(status.getTweets().get(i).getA() + "\n \t" + status.getTweets().get(i).getB());
				twitterScroll.addView(tweetList.get(i));
			}
		} catch (TwitterException e) {
			Toast.makeText(this, "Could not load tweets", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
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
}
