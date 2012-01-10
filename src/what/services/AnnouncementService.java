package what.services;

import what.announcements.AnnouncementActivity;
import what.announcements.BlogActivity;
import what.settings.Settings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import api.announcements.Announcements;

public class AnnouncementService extends Service {
	public static final String BROADCAST_ACTION = "";
	private final Handler handler = new Handler();
	private Intent intent;
	private NotificationManager myNotificationManager;
	public static Announcements announcements;
	private static boolean isRunning = false;
	private int numberOfA;
	private int numberOfB;
	public static int IDA = 2;
	public static int IDB = 3;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {
		super.onCreate();

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		intent = new Intent();
		setRunning(true);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 0); // initial delay
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		@Override
		public void run() {
			try {
				updateStatusBar();
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendBroadcast(intent);
			handler.postDelayed(this, loadRefreshRate()); // delay
		}
	};

	private void updateStatusBar() {
		new LoadAnnouncements().execute();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		setRunning(false);
		super.onDestroy();
	}

	private void generateNotificationA(CharSequence ticket, CharSequence title, CharSequence content) {
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(what.gui.R.drawable.icon, ticket, when);
		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(this, AnnouncementActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, title, content, contentIntent);

		myNotificationManager.notify(IDA, notification);
	}

	private void generateNotificationB(CharSequence ticket, CharSequence title, CharSequence content) {
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(what.gui.R.drawable.icon, ticket, when);
		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(this, BlogActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, title, content, contentIntent);

		myNotificationManager.notify(IDB, notification);
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public static void setRunning(boolean isRunning) {
		AnnouncementService.isRunning = isRunning;
	}

	private long loadRefreshRate() {
		try {
			return Long.parseLong(sharedPreferences.getString("announcementsService_interval", "180")) * 60000;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 10800000;
		}
	}

	private class LoadAnnouncements extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			announcements = Announcements.init();

			if (Settings.getNumberOfAnnouncements() == 0) {
				numberOfA = announcements.getResponse().getNumberOfAnnouncements();
			} else {
				numberOfA = Settings.getNumberOfAnnouncements();
			}
			if (Settings.getNumberOfBlogPosts() == 0) {
				numberOfB = announcements.getResponse().getNumberOfBlogPosts();
			} else {
				numberOfB = Settings.getNumberOfBlogPosts();
			}
			return announcements.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				if (numberOfA > announcements.getResponse().getNumberOfAnnouncements()) {
					generateNotificationA("New Announcement!", "", "");
					numberOfA = announcements.getResponse().getNumberOfAnnouncements();
					Settings.saveNumberOfAnnouncements(numberOfA);
					Settings.commit();
				}
				if (numberOfB > announcements.getResponse().getNumberOfBlogPosts()) {
					generateNotificationB("New Blog Post!", "", "");
					numberOfB = announcements.getResponse().getNumberOfBlogPosts();
					Settings.saveNumberOfBlogPosts(numberOfB);
					Settings.commit();
				}
			}
		}
	}
}