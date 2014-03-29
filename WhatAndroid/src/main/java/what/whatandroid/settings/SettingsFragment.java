package what.whatandroid.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import what.whatandroid.R;
import what.whatandroid.updater.UpdateService;

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
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		//If the version number is clicked launch an update check
		if (preference.getKey() != null && preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_version_name))){
			if (getActivity() != null){
				Intent checkUpdates = new Intent(getActivity(), UpdateService.class);
				getActivity().startService(checkUpdates);
				return true;
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
