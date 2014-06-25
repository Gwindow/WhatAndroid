package what.whatandroid.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import api.son.MySon;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.settings.SettingsFragment;

import java.net.HttpCookie;

/**
 * LoginTask that takes care of logging in the user using either an existing cookie
 * or the passed username and password. Params to execute should be: username, password
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {
	private ProgressDialog dialog;
	private Context context;

	/**
	 * Construct the login task giving it the context to load the default shared preferences from
	 *
	 * @param context context to load preferences from. Must not be null
	 */
	public LoginTask(Context context){
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(String... params){
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String cookieJson = preferences.getString(SettingsFragment.USER_COOKIE, null);
			if (cookieJson != null){
				HttpCookie cookie = (HttpCookie)MySon.toObjectFromString(cookieJson, HttpCookie.class);
				if (loginWithCookie(cookie)){
					return true;
				}
				//If the cookie is expired or invalid then remove it and login normally
				else {
					preferences.edit().remove(SettingsFragment.USER_COOKIE).commit();
				}
			}
			MySoup.login("login.php", params[0], params[1], true);
			cookieJson = MySon.toJson(MySoup.getSessionCookie(), HttpCookie.class);
			preferences.edit()
				.putString(SettingsFragment.USER_COOKIE, cookieJson)
				.putString(SettingsFragment.USER_NAME, params[0])
				.putString(SettingsFragment.USER_PASSWORD, params[1])
				.commit();
			if (MySoup.isNotificationsEnabled()){
				preferences.edit()
					.putBoolean(context.getString(R.string.key_pref_new_notifications),
						MySoup.getIndex().getResponse().getNotifications().hasNewNotifications())
					.commit();
			}
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Attempt to login using the user's existing cookie
	 *
	 * @return true if successful
	 */
	private boolean loginWithCookie(HttpCookie cookie){
		try {
			if (cookie != null && !cookie.hasExpired()){
				MySoup.addCookie(cookie);
				MySoup.loadIndex();
				return true;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPreExecute(){
		dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setMessage("Logging in...");
		dialog.show();
	}

	@Override
	protected void onPostExecute(Boolean status){
		dismissDialog();
	}

	@Override
	protected void onCancelled(Boolean aBoolean){
		dismissDialog();
	}

	/**
	 * Explicitly dismiss the progress dialog. This should be done if the view is being destroyed
	 */
	public void dismissDialog(){
		if (dialog.isShowing()){
			dialog.cancel();
		}
	}
}
