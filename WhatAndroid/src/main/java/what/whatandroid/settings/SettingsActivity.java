package what.whatandroid.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import what.whatandroid.R;

/**
 * Activity for changing user preferences
 * TODO: If we can drop API<11 we should migrate to a PreferenceFragment
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * Key in the shared preferences file where the user cookie is stored
	 */
	public static final String USER_COOKIE = "pref_user_cookie", USER_NAME = "pref_user_name",
		USER_PASSWORD = "pref_user_password";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
