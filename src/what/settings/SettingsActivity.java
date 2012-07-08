package what.settings;

import what.gui.R;
import what.inbox.ReportActivity;
import what.login.UpdateChecker;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import api.son.MySon;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnPreferenceClickListener {
	private Preference debugPreference;
	private Preference reportPreference;

	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO set theme
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Settings");

		addPreferencesFromResource(R.xml.settingsactivity);

		PreferenceScreen prefenceScreen = getPreferenceScreen();
		PreferenceCategory preferenceCategory = new PreferenceCategory(this);
		// TODO fix
		preferenceCategory.setTitle("Version " + getInstalledVersion());
		prefenceScreen.addPreference(preferenceCategory);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		debugPreference = findPreference("debug_preference");
		debugPreference.setOnPreferenceClickListener(this);

		reportPreference = findPreference("report_preference");
		reportPreference.setOnPreferenceClickListener(this);
	}

	private double getInstalledVersion() {
		int versionCode;
		String versionName;
		double installedVersion = 0;
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionCode = manager.versionCode;
			versionName = manager.versionName;
			installedVersion = versionCode + Double.parseDouble(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// TODO remove
		// return installedVersion;
		return UpdateChecker.VERSION;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		if (pref == debugPreference) {
			if (sharedPreferences.getBoolean("debug_preference", true)) {
				MySon.setDebugEnabled(true);
			} else {
				MySon.setDebugEnabled(false);
			}
		}
		if (pref == debugPreference) {
			if (sharedPreferences.getBoolean("debug_preference", true)) {
				MySon.setDebugEnabled(true);
			} else {
				MySon.setDebugEnabled(false);
			}
		}
		if (pref == reportPreference) {
			Intent intent = new Intent(SettingsActivity.this, ReportActivity.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	public void onPause() {
		Toast.makeText(SettingsActivity.this, "Settings Saved", Toast.LENGTH_SHORT).show();
		super.onPause();
	}

}