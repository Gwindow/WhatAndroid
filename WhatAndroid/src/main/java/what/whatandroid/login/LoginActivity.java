package what.whatandroid.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.announcements.AnnouncementsActivity;


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
		MySoup.setSite("192.168.124.137", false);
	}


	@Override
	public void onClick(View v) {
		//The only thing being listened to for clicks in the view is the login button, so skip checking who was clicked
		if (username.length() > 0 && password.length() > 0){
			//TODO: Should show a progress dialog and proper error dialog instead of Toasts
			Toast.makeText(this, "Logging you in, " + username.getText().toString(), Toast.LENGTH_LONG).show();
			new Login().execute(username.getText().toString(), password.getText().toString());
		}
		else {
			Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Login async task, takes user's username and password and logs them
	 * into the site via the api library
	 */
	private class Login extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			String name = params[0];
			String pwd = params[1];
			try {
				MySoup.login("login.php", name, pwd, false);
				return true;
			}
			catch (Exception e){
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status){
				Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this, AnnouncementsActivity.class);
				startActivity(intent);
			}
			else {
				Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
			}
		}
	}
}