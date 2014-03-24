package what.whatandroid.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import api.son.MySon;
import api.soup.MySoup;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import what.whatandroid.R;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.settings.SettingsActivity;

import java.net.HttpCookie;

/**
 * The login fragment, provides the user fields for their user name
 * and password and allows them to log in to the site
 */
public class LoginActivity extends Activity implements View.OnClickListener {
	//TODO: Developers put your local Gazelle install IP here instead of testing on the live site
	//I recommend setting up with Vagrant: https://github.com/dr4g0nnn/VagrantGazelle
	public static final String SITE = "192.168.1.125:8080/";
	//public static final String SITE = "what.cd";
	/**
	 * Set this parameter to true in the intent if we just want the login activity to
	 * log the user in then return back to the launching activity
	 */
	public static final String LOGIN_REQUEST = "what.whatandroid.LOGIN_REQUEST";
	/**
	 * If we're logging in at the request of some other activity
	 */
	private boolean loginRequest;
	private TextView username, password;

	/**
	 * Perform the setup of the site we'll be making API requests to and get MySoup configured
	 * properly. Also sets up universal image loader for the application
	 */
	public static void setupSite(Context context){
		MySoup.setSite(SITE, false);
		MySoup.setUserAgent("WhatAndroid Android");
		MySoup.setAndroid(true);

		//Setup Universal Image loader global config
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
			.discCacheExtraOptions(512, 512, Bitmap.CompressFormat.JPEG, 75, null)
			.discCacheSize(50 * 512 * 512)
			.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (TextView)findViewById(R.id.username_input);
		password = (TextView)findViewById(R.id.password_input);
		Button login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);
		setupSite(this);

		//Setup saved user name and password if we've got them
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserName = preferences.getString(SettingsActivity.USER_NAME, "");
		String savedUserPass = preferences.getString(SettingsActivity.USER_PASSWORD, "");
		username.setText(savedUserName);
		password.setText(savedUserPass);

		loginRequest = getIntent().getBooleanExtra(LOGIN_REQUEST, false);
		System.out.println("Login request: " + (loginRequest ? "true" : "false"));
	}

	@Override
	protected void onResume(){
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getString(SettingsActivity.USER_COOKIE, null) != null){
			new Login().execute(username.getText().toString(), password.getText().toString());
		}
	}

	@Override
	public void onClick(View v) {
		if (username.length() > 0 && password.length() > 0){
			//Check if we're logging in with a different user than we saved previously
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String savedUserName = preferences.getString(SettingsActivity.USER_NAME, "");
			if (!username.getText().toString().equalsIgnoreCase(savedUserName)){
				preferences.edit()
					.remove(SettingsActivity.USER_NAME)
					.remove(SettingsActivity.USER_PASSWORD)
					.remove(SettingsActivity.USER_COOKIE)
					.commit();
			}

			new Login().execute(username.getText().toString(), password.getText().toString());
		}
		else {
			Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
			username.requestFocus();
		}
	}

	/**
	 * Login async task, takes user's username and password and logs them
	 * into the site via the api library
	 */
	public class Login extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected Boolean doInBackground(String... params){
			try {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
				String cookieJson = preferences.getString(SettingsActivity.USER_COOKIE, null);
				if (cookieJson != null){
					HttpCookie cookie = (HttpCookie)MySon.toObjectFromString(cookieJson, HttpCookie.class);
					if (cookie != null && !cookie.hasExpired()){
						MySoup.addCookie(cookie);
						MySoup.loadIndex();
						System.out.println("Using saved cookie");
						return true;
					}
				}
				MySoup.login("login.php", params[0], params[1], true);
				cookieJson = MySon.toJson(MySoup.getSessionCookie(), HttpCookie.class);
				preferences.edit()
					.putString(SettingsActivity.USER_COOKIE, cookieJson)
					.putString(SettingsActivity.USER_NAME, params[0])
					.putString(SettingsActivity.USER_PASSWORD, params[1])
					.commit();
				return true;
			}
			catch (Exception e){
				return false;
			}
		}

		@Override
		protected void onPreExecute(){
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Logging in...");
			dialog.show();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status){
				if (loginRequest){
					System.out.println("Returning to requesting activity");
					Intent result = new Intent();
					setResult(Activity.RESULT_OK, result);
					LoginActivity.this.finish();
				}
				else {
					Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				}
			}
			else {
				Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
			}
		}
	}
}