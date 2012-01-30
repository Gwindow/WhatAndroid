package what.announcements;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import what.services.AnnouncementService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.announcements.Announcements;
import api.announcements.AnnouncementsList;

public class AnnouncementActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> list = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private Announcements announcements;
	private Intent intent;
	private ProgressDialog dialog;
	private NotificationManager myNotificationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.announcements, true);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		myNotificationManager.cancel(AnnouncementService.IDA);
		if (AnnouncementService.isRunning()) {
			announcements = AnnouncementService.announcements;
			populateLayout();
		}
		if (!AnnouncementService.isRunning()) {
			new LoadAnnouncements().execute();
		}
	}

	private void populateLayout() {
		List<AnnouncementsList> a = announcements.getResponse().getAnnouncements();
		for (int i = 0; i < a.size(); i++) {
			if ((i % 2) == 0) {
				list.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				list.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			list.get(i).setText(a.get(i).getTitle());
			list.get(i).setId(i);
			list.get(i).setOnClickListener(this);
			scrollLayout.addView(list.get(i));
		}
	}

	public void openAnnouncement(int i) {
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (list.size()); i++) {
			if (v.getId() == list.get(i).getId()) {
				openAnnouncement(i);
			}
		}
	}

	private class LoadAnnouncements extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(AnnouncementActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			announcements = Announcements.init();
			return announcements.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			if (status == false) {
				Toast.makeText(AnnouncementActivity.this, "Could not load announcements", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
