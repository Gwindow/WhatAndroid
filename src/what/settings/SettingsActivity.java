package what.settings;

import java.util.LinkedList;

import what.gui.R;
import what.inbox.ReportActivity;
import what.login.UpdateChecker;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import api.son.MySon;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnPreferenceClickListener {
	private Preference debugPreference;
	private Preference reportPreference;
	private LinkedList<CheckBoxPreference> themePreferencesList;
	private LinkedList<CheckBoxPreference> iconPreferencesList;
	private SharedPreferences sharedPreferences;
	private boolean themeMessageShown;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO set theme
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Settings");
		getSupportActionBar().setLogo(Settings.getHomeIconPath());

		addPreferencesFromResource(R.xml.settingsactivity);

		themePreferencesList = new LinkedList<CheckBoxPreference>();
		iconPreferencesList = new LinkedList<CheckBoxPreference>();

		PreferenceScreen prefenceScreen = getPreferenceScreen();
		PreferenceCategory preferenceCategory = new PreferenceCategory(this);
		// TODO fix
		preferenceCategory.setTitle("Version " + getInstalledVersion());
		prefenceScreen.addPreference(preferenceCategory);

		populateThemes();
		populateIcons();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		debugPreference = findPreference("debug_preference");
		debugPreference.setOnPreferenceClickListener(this);

		reportPreference = findPreference("report_preference");
		reportPreference.setOnPreferenceClickListener(this);
	}

	private void populateThemes() {
		PreferenceScreen prefenceScreen = getPreferenceScreen();
		PreferenceCategory preferenceCategory = new PreferenceCategory(this);
		preferenceCategory.setTitle("Themes");
		prefenceScreen.addPreference(preferenceCategory);

		for (String title : Settings.themes.keySet()) {
			CheckBoxPreference preference = new CheckBoxPreference(this);
			preference.setTitle(title);
			int saved_theme = Settings.getTheme().getA();
			Log.d("settings", String.valueOf(Settings.getTheme().getA() + " and " + Settings.themes.get(title).getA()));
			if (String.valueOf(saved_theme).trim().equalsIgnoreCase(String.valueOf(Settings.themes.get(title).getA()).trim())) {
				Log.d("settings", "matches");
				preference.setChecked(true);
			}
			preference.setOnPreferenceClickListener(this);
			preferenceCategory.addPreference(preference);
			themePreferencesList.add(preference);
		}

	}

	private void populateIcons() {
		PreferenceScreen prefenceScreen = getPreferenceScreen();
		PreferenceCategory preferenceCategory = new PreferenceCategory(this);
		preferenceCategory.setTitle("Actionbar Icon");
		prefenceScreen.addPreference(preferenceCategory);

		for (String title : Settings.icons.keySet()) {
			CheckBoxPreference preference = new CheckBoxPreference(this);
			preference.setTitle(title);
			if (Settings.getHomeIconPath() == Settings.icons.get(title)) {
				preference.setChecked(true);
			}
			preference.setOnPreferenceClickListener(this);
			preferenceCategory.addPreference(preference);
			iconPreferencesList.add(preference);
		}

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
		if (Settings.themes.containsKey(pref.getTitle())) {
			Settings.saveTheme(Settings.themes.get(pref.getTitle()).getA(), Settings.themes.get(pref.getTitle()).getB());
			for (CheckBoxPreference cbp : themePreferencesList) {
				if (cbp != pref) {
					cbp.setChecked(false);
				}
			}
			if (!themeMessageShown) {
				Toast.makeText(this, "Theme changes will be applied next time you load a page", Toast.LENGTH_LONG).show();
				themeMessageShown = true;
			}
		}

		if (Settings.icons.containsKey(pref.getTitle())) {
			Settings.saveHomeIconPath(Settings.icons.get(pref.getTitle()));
			for (CheckBoxPreference cbp : iconPreferencesList) {
				if (cbp != pref) {
					cbp.setChecked(false);
				}
			}
		}

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