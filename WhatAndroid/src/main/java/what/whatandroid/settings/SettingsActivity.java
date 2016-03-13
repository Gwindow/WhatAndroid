package what.whatandroid.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import what.whatandroid.R;

/**
 * Activity for changing user settings
 */
public class SettingsActivity extends Activity implements FolderPickerDialog.FolderPickerCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
	}

	/**
	 * See if images are enabled for the context. We also cache the result so we don't need to do the
	 * check so often
	 *
	 * @return true if enabled, false if not
	 */
	public static Boolean imagesEnabled(Context context){
		//If there's no context don't load images since we're navigating away
		if (context == null){
			return false;
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean wifiOnly = preferences.getBoolean(context.getString(R.string.key_pref_img_wifi), false);
		if (wifiOnly){
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return wifi.isConnected();
		}
		return true;
	}

	@Override
	public void pickFolder(String folder){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.edit().putString(getString(R.string.key_pref_torrent_download_path), folder).apply();
	}

	/**
	 * See if light layout for forums is enabled for the context.
     *
     * @return true if enabled, false if not
     */
    public static Boolean lightLayoutEnabled(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getBoolean(context.getString(R.string.key_pref_light_layout), false);
    }

	/**
	 * See if light theme is enabled for the context.
	 *
	 * @return true if enabled, false if not
	 */
	public static Boolean lightThemeEnabled(Context context){
		if (context == null){
			return false;
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(context.getString(R.string.key_pref_light_theme), false);
	}

	public static String torrentDownloadPath(Context context){
		if (context == null){
			return "";
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(context.getString(R.string.key_pref_torrent_download_path), "");
	}
}
