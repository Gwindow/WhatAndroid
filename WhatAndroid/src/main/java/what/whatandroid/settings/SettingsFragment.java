package what.whatandroid.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import what.whatandroid.R;

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
}
