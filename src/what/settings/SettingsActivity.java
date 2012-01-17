package what.settings;

import what.gui.R;
import what.services.AnnouncementService;
import what.services.InboxService;
import what.services.NotificationService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import api.soup.MySoup;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener {
	// UI
	private Preference quickSearch_preference, spotifyButton_preference, lastfmButton_preference;
	// Services
	private Preference announcementsService_preference, inboxService_preference, notificationsService_preference;
	// Refresh Intervals
	private Preference announcementsService_interval, inboxService_interval, notificationsService_interval;

	private SharedPreferences sharedPreferences;

	private Intent inboxService;
	private Intent notificationService;
	private Intent annoucementService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settingsactivity);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		quickSearch_preference = findPreference("quickSearch_preference");
		quickSearch_preference.setOnPreferenceClickListener(this);

		spotifyButton_preference = findPreference("spotifyButton_preference");
		spotifyButton_preference.setOnPreferenceClickListener(this);

		lastfmButton_preference = findPreference("lastfmButton_preference");
		lastfmButton_preference.setOnPreferenceClickListener(this);

		announcementsService_preference = findPreference("announcementsService_preference");
		announcementsService_preference.setOnPreferenceClickListener(this);

		inboxService_preference = findPreference("inboxService_preference");
		inboxService_preference.setOnPreferenceClickListener(this);

		notificationsService_preference = findPreference("notificationsService_preference");
		notificationsService_preference.setOnPreferenceClickListener(this);

		Log.v("Background resource ID", Integer.toString(backgroundFromPreference(this)));

		annoucementService = new Intent(this, AnnouncementService.class);
		inboxService = new Intent(this, InboxService.class);
		notificationService = new Intent(this, NotificationService.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.Preference.OnPreferenceClickListener#onPreferenceClick(android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceClick(Preference pref) {
		if (pref == announcementsService_preference) {
			if (sharedPreferences.getBoolean("announcementsService_preference", true) == true && !AnnouncementService.isRunning()) {
				try {
					startService(annoucementService);
				} catch (Exception e) {
					Toast.makeText(this, "Could not start service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				try {
					stopService(annoucementService);
				} catch (Exception e) {
					Toast.makeText(this, "Could not stop service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}
		if (pref == inboxService_preference) {
			if (sharedPreferences.getBoolean("inboxService_preference", true) == true && !InboxService.isRunning()) {
				try {
					startService(inboxService);
					Toast.makeText(this, sharedPreferences.getString("inboxService_interval", "-1"), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(this, "Could not start service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

			} else {
				try {
					stopService(inboxService);
				} catch (Exception e) {
					Toast.makeText(this, "Could not stop service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}
		if (pref == notificationsService_preference) {
			if (sharedPreferences.getBoolean("notificationsService_preference", true) == true && !NotificationService.isRunning()
					&& MySoup.canNotifications()) {
				try {
					startService(notificationService);
				} catch (Exception e) {
					Toast.makeText(this, "Could not start service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				try {
					stopService(notificationService);
				} catch (Exception e) {
					Toast.makeText(this, "Could not stop service", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static int backgroundFromPreference(Context context) {
		int resId =
				Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("background_list_preference",
						"0"));
		switch (resId) {
		case 1:
			return R.drawable.wood;
		case 2:
			// return some other background's resource ID.
		case 3:
			// ...
		case 0:
		default:
			return R.drawable.wood2;
		}
	}

}