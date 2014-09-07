package what.whatandroid.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import what.whatandroid.R;
import what.whatandroid.updater.UpdateBroadcastReceiver;
import what.whatandroid.updater.UpdateService;
import what.whatandroid.updater.VersionNumber;

/**
 * Fragment containing the user's settings & preferences
 */
public class SettingsFragment extends PreferenceFragment {
	/**
	 * Key in the shared preferences file where the user cookie is stored
	 */
	public static final String USER_COOKIE = "pref_user_cookie", USER_NAME = "pref_user_name",
		USER_PASSWORD = "pref_user_password";


	public SettingsFragment(){
		//required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		try {
			Preference version = getPreferenceScreen().findPreference(getString(R.string.key_pref_version_name));
			String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			VersionNumber versionNumber = new VersionNumber(versionName);
			version.setTitle(versionNumber.toString());
		}
		catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		if (preference.getKey() != null && getActivity() != null){
			//If the version number is clicked launch an update check
			if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_version_name))){
				Toast.makeText(getActivity(), "Checking for updates", Toast.LENGTH_SHORT).show();
				Intent checkUpdates = new Intent(getActivity(), UpdateService.class);
				getActivity().startService(checkUpdates);
				return true;
			}
			//If we're enabling or disabling the periodic update checker then cancel or recreate the alarm as necessary
			else if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_disable_updater))){
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				Intent updater = new Intent(getActivity(), UpdateBroadcastReceiver.class);
				PendingIntent pending = PendingIntent.getBroadcast(getActivity(), 2, updater, PendingIntent.FLAG_NO_CREATE);
				AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
				boolean checkerDisabled = preferences.getBoolean(getActivity().getString(R.string.key_pref_disable_updater), false);
				//Cancel the alarm if we're disabling the checker
				if (pending != null && checkerDisabled){
					alarmMgr.cancel(pending);
				}
				//Set the alarm if we're re-enabling it and it was removed (ie. pending == null)
				else {
					pending = PendingIntent.getBroadcast(getActivity(), 2, updater, 0);
					alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_DAY,
						AlarmManager.INTERVAL_DAY, pending);
				}
				return true;
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
