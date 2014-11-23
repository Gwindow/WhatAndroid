package what.whatandroid.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.net.HttpCookie;

import api.son.MySon;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.settings.SettingsFragment;

/**
 * LoginTask that takes care of logging in the user using either an existing cookie
 * or the passed username and password. Params to execute should be: username, password
 */
public class LoginTask extends AsyncTask<String, Void, LoginTask.Status> {
	private ProgressDialog dialog;
	private Context context;

	public static enum Status {
		OK, COOKIE_EXPIRED, FAILED
	}

	/**
	 * Construct the login task giving it the context to load the default shared preferences from
	 *
	 * @param context context to load preferences from. Must not be null
	 */
	public LoginTask(Context context){
		this.context = context;
	}

	@Override
	protected Status doInBackground(String... params){
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String cookieJson = preferences.getString(SettingsFragment.USER_COOKIE, null);
			if (cookieJson != null){
				HttpCookie cookie = (HttpCookie)MySon.toObjectFromString(cookieJson, HttpCookie.class);
				if (loginWithCookie(cookie)){
					return Status.OK;
				}
				//If the cookie is expired or invalid then remove it and login normally
				else {
					preferences.edit().remove(SettingsFragment.USER_COOKIE).apply();
				}
			}
			//If the cookie expired and we don't have a password to login, bail out
			//and tell them the cookie is dead
			if (params.length < 2 || params[1] == null || params[1].isEmpty()){
				dialog.dismiss();
				return Status.COOKIE_EXPIRED;
			}
			MySoup.login("login.php", params[0], params[1], true);
			cookieJson = MySon.toJson(MySoup.getSessionCookie(), HttpCookie.class);
			preferences.edit()
					.putString(SettingsFragment.USER_COOKIE, cookieJson)
					.putString(SettingsFragment.USER_NAME, params[0])
					.apply();
			if (MySoup.isNotificationsEnabled()){
				preferences.edit()
						.putInt(context.getString(R.string.key_pref_num_notifications),
								MySoup.getIndex().getResponse().getNotifications().getTorrentNotifications().intValue())
						.apply();
			}
			preferences.edit()
					.putBoolean(context.getString(R.string.key_pref_new_subscriptions),
							MySoup.getIndex().getResponse().getNotifications().hasNewSubscriptions())
					.putInt(context.getString(R.string.key_pref_new_messages),
							MySoup.getIndex().getResponse().getNotifications().getMessages().intValue())
					.apply();
			return Status.OK;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return Status.FAILED;
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
				return MySoup.getIndex() != null;
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
}
