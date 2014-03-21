package what.whatandroid.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;
import what.whatandroid.settings.SettingsActivity;

/**
 * The login fragment, provides the user fields for their user name
 * and password and allows them to log in to the site
 */
public class LoginActivity extends Activity implements View.OnClickListener {
	private TextView username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (TextView)findViewById(R.id.username_input);
		password = (TextView)findViewById(R.id.password_input);
		Button login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);
		//TODO: Developers put your local Gazelle install IP here instead of testing on the live site
		//I recommend setting up with Vagrant: https://github.com/dr4g0nnn/VagrantGazelle
		//MySoup.setSite("192.168.1.125:8080", false);
		MySoup.setSite("what.cd", true);
		MySoup.setUserAgent("WhatAndroid Android");
		MySoup.setAndroid(true);

		//Setup Universal Image loader global config
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.discCacheSize(20 * 512 * 512)
			.build();
		ImageLoader.getInstance().init(config);

		//Setup saved user name and password if we've got them
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserName = preferences.getString(SettingsActivity.USER_NAME, "");
		String savedUserPass = preferences.getString(SettingsActivity.USER_PASSWORD, "");
		username.setText(savedUserName);
		password.setText(savedUserPass);
	}

	@Override
	protected void onResume(){
		super.onResume();
		/*
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getString(SettingsActivity.USER_COOKIE, null) != null){
			new Login().execute(username.getText().toString(), password.getText().toString());
		}
		*/
	}

	@Override
	public void onClick(View v) {
		//The only thing being listened to for clicks in the view is the login button, so skip checking who was clicked
		if (username.length() > 0 && password.length() > 0){
			Toast.makeText(this, "Logging you in: " + username.getText().toString(), Toast.LENGTH_SHORT).show();
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
	private class Login extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//If there's a saved cookie then use that and load the index
				//TODO: Handle cookie expiration
				MySoup.login("login.php", params[0], params[1], true);
				//TODO: Save the cookie, user name and password
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
				Intent intent = new Intent(LoginActivity.this, AnnouncementsActivity.class);
				startActivity(intent);
			}
			else {
				Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
			}
		}
	}
}