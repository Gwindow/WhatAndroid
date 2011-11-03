package what.services;

import what.notifications.NotificationsActivity;
import what.settings.Settings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import api.notifications.Notifications;

public class NotificationService extends Service {
	public static final String BROADCAST_ACTION = "";
	private final Handler handler = new Handler();
	private Intent intent;
	private NotificationManager myNotificationManager;
	public static Notifications notifications;
	public static int ID = 4;
	private static boolean isRunning = false;

	@Override
	public void onCreate() {
		super.onCreate();
		setRunning(true);
		intent = new Intent();
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
			handler.postDelayed(this, Settings.getNotificationRefreshRate()); // delay
		}
	};

	private void updateStatusBar() {
		new LoadNotifications().execute();
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

	private void generateNotification(CharSequence ticket, CharSequence title, CharSequence content) {
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(what.gui.R.drawable.icon, ticket, when);
		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(this, NotificationsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, title, content, contentIntent);

		myNotificationManager.notify(ID, notification);
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public static void setRunning(boolean isRunning) {
		NotificationService.isRunning = isRunning;
	}

	private class LoadNotifications extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			notifications = Notifications.notificationsFromPage(1);
			return notifications.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				generateNotification("Torrent Notification", notifications.getResponse().getNumNew() + " new notifications", "");
			}
		}
	}
}