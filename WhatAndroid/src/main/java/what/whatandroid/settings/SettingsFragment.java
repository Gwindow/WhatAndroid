package what.whatandroid.settings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.inbox.conversation.ManageConversationDialog;
import what.whatandroid.login.LoginActivity;
import what.whatandroid.updater.UpdateBroadcastReceiver;
import what.whatandroid.updater.UpdateService;
import what.whatandroid.updater.VersionNumber;

/**
 * Fragment containing the user's settings & preferences
 */
public class SettingsFragment extends PreferenceFragment implements FragmentCompat.OnRequestPermissionsResultCallback {
    /**
     * Key in the shared preferences file where the user cookie is stored
     */
    public static final String USER_COOKIE = "pref_user_cookie", USER_NAME = "pref_user_name",
            USER_PASSWORD = "pref_user_password";
    private static final int PERMISSIONS_REQUEST_WRITE_EXT_STORAGE = 1;
    /** Strings to pass the download dirs to the permission request if needed */
    String folder_picker_dir, folder_picker_download_dir;

    public SettingsFragment() {
        //required empty ctor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        try {
            Preference version = getPreferenceScreen().findPreference(getString(R.string.key_pref_version_name));
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            VersionNumber versionNumber = new VersionNumber(versionName);
            version.setTitle(versionNumber.toString());
            getActivity().setTheme(R.style.AppTheme);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXT_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            FolderPickerDialog dialog = FolderPickerDialog.newInstance(folder_picker_dir, folder_picker_download_dir);
            dialog.show(getFragmentManager(), "folder_picker");
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey() != null && getActivity() != null) {
            if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_torrent_download_path))) {
                folder_picker_download_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                folder_picker_dir = preference.getSharedPreferences().getString(preference.getKey(), folder_picker_download_dir);
                // Check that we've got permissions to view the files
                int permission_check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission_check == PackageManager.PERMISSION_GRANTED){
                    FolderPickerDialog dialog = FolderPickerDialog.newInstance(folder_picker_dir, folder_picker_download_dir);
                    dialog.show(getFragmentManager(), "folder_picker");
                }
                else {
                    FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXT_STORAGE);
                }
                return true;
            }
            //If the version number is clicked launch an update check
            if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_version_name))) {
                Toast.makeText(getActivity(), "Checking for updates", Toast.LENGTH_SHORT).show();
                Intent checkUpdates = new Intent(getActivity(), UpdateService.class);
                getActivity().startService(checkUpdates);
                return true;
            }
            if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_light_theme))) {
                if (SettingsActivity.lightThemeEnabled(getActivity().getApplicationContext())) {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                }
            }

            //If we're enabling or disabling the periodic update checker then cancel or recreate the alarm as necessary
            else if (preference.getKey().equalsIgnoreCase(getString(R.string.key_pref_disable_updater))) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Intent updater = new Intent(getActivity(), UpdateBroadcastReceiver.class);
                PendingIntent pending = PendingIntent.getBroadcast(getActivity(), 2, updater, PendingIntent.FLAG_NO_CREATE);
                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                boolean checkerDisabled = preferences.getBoolean(getActivity().getString(R.string.key_pref_disable_updater), false);
                //Cancel the alarm if we're disabling the checker
                if (pending != null && checkerDisabled) {
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
