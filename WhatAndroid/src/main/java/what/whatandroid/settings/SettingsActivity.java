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
public class SettingsActivity extends Activity {

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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean wifiOnly = preferences.getBoolean(context.getString(R.string.key_pref_img_wifi), false);
		if (wifiOnly){
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return wifi.isConnected();
		}
		return true;
	}
}
