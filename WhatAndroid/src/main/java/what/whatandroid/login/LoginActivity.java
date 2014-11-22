package what.whatandroid.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import what.whatandroid.R;
import what.whatandroid.barcode.BarcodeActivity;
import what.whatandroid.profile.ProfileActivity;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.settings.SettingsFragment;

/**
 * The login fragment, provides the user fields for their user name
 * and password and allows them to log in to the site
 */
public class LoginActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {
	/**
	 * Set this parameter to true in the intent if we just want the login activity to
	 * log the user in then return back to the launching activity
	 */
	public static final String LOGIN_REQUEST = "what.whatandroid.LOGIN_REQUEST";
	/**
	 * If we're logging in at the request of some other activity
	 */
	private boolean loginRequest;
	private Login loginTask;
	private TextView username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (TextView)findViewById(R.id.username_input);
		password = (TextView)findViewById(R.id.password_input);
		Button login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);
		LoggedInActivity.initSoup();
		LoggedInActivity.initImageLoader(this);
		LoggedInActivity.launchServices(this);

		//Setup saved user name and password if we've got them
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserName = preferences.getString(SettingsFragment.USER_NAME, "");
		username.append(savedUserName);
		password.setOnEditorActionListener(this);

		loginRequest = getIntent().getBooleanExtra(LOGIN_REQUEST, false);
		//Remove the saved user password from the settings, it's not secure
		preferences.edit()
			.remove(SettingsFragment.USER_PASSWORD)
			.apply();
	}

	@Override
	protected void onResume(){
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getString(SettingsFragment.USER_COOKIE, null) != null){
			loginTask = new Login();
			loginTask.execute(username.getText().toString(), password.getText().toString());
		}
	}

	@Override
	protected void onPause(){
		if (loginTask != null){
			loginTask.cancel(true);
		}
		super.onPause();
	}

	@Override
	public void onClick(View v){
		if (username.length() > 0 && password.length() > 0){
			//Check if we're logging in with a different user than we saved previously
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String savedUserName = preferences.getString(SettingsFragment.USER_NAME, "");
			if (!username.getText().toString().equalsIgnoreCase(savedUserName)){
				preferences.edit()
					.remove(SettingsFragment.USER_NAME)
					.remove(SettingsFragment.USER_COOKIE)
					.apply();
			}
			loginTask = new Login();
			loginTask.execute(username.getText().toString(), password.getText().toString());
		}
		else {
			Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
			username.requestFocus();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
		if (event == null || event.getAction() == KeyEvent.ACTION_DOWN){
			if (username.length() > 0 && password.length() > 0){
				//Check if we're logging in with a different user than we saved previously
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				String savedUserName = preferences.getString(SettingsFragment.USER_NAME, "");
				if (!username.getText().toString().equalsIgnoreCase(savedUserName)){
					preferences.edit()
						.remove(SettingsFragment.USER_NAME)
						.remove(SettingsFragment.USER_COOKIE)
						.apply();
				}
				loginTask = new Login();
				loginTask.execute(username.getText().toString(), password.getText().toString());
			}
			else {
				Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
				username.requestFocus();
			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent;
		switch (item.getItemId()){
			case R.id.action_barcodes:
				intent = new Intent(this, BarcodeActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_settings:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_feedback:
				intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "whatcdandroid@gmail.com", null));
				intent.putExtra(Intent.EXTRA_SUBJECT, "WhatAndroid Feedback");
				startActivity(Intent.createChooser(intent, "Send email"));
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){
		if (loginRequest){
			Intent result = new Intent();
			setResult(Activity.RESULT_CANCELED, result);
			LoginActivity.this.finish();
		}
		else {
			super.onBackPressed();
		}
	}

	/**
	 * Login async task, takes user's username and password and logs them
	 * into the site via the api library
	 */
	private class Login extends LoginTask {

		public Login(){
			super(LoginActivity.this);
		}

		@Override
		protected void onPostExecute(Status status){
			super.onPostExecute(status);
			if (status == Status.OK){
				if (loginRequest){
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
			else if (status == Status.COOKIE_EXPIRED){
				Toast.makeText(LoginActivity.this, "Cookie expired, please login", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(LoginActivity.this, "Could not login", Toast.LENGTH_LONG).show();
			}
		}
	}
}