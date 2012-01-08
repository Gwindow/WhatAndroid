package what.settings;

import what.gui.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settingsactivity);
		Log.v("Background resource ID", Integer.toString(backgroundFromPreference(this)));
	}

	public static int backgroundFromPreference(Context mCtx) {
		int resId =
				Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mCtx).getString("background_list_preference", "0"));
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