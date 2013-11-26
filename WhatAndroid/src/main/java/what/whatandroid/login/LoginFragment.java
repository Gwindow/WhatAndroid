package what.whatandroid.login;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import what.whatandroid.FragmentHost;
import what.whatandroid.R;
import what.whatandroid.home.HomeFragment;

/**
 * The login fragment, provides the user fields for their user name
 * and password and allows them to log in to the site
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
	private TextView username, password;
	private FragmentHost host;

	public static String NAME = "Login";

	public LoginFragment(){
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login_layout, container, false);
		username = (TextView)v.findViewById(R.id.username_input);
		password = (TextView)v.findViewById(R.id.password_input);
		Button login = (Button)v.findViewById(R.id.login_button);
		login.setOnClickListener(this);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			host = (FragmentHost)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement FragmentHost");
		}
	}

	@Override
	public void onClick(View v) {
		if (username.length() > 0 && password.length() > 0){
			new Login().execute(username.getText().toString(), password.getText().toString());
		}
		else {
			Toast.makeText(getActivity(), "Please enter your username and password", Toast.LENGTH_SHORT).show();
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
				host.replaceFragment(new HomeFragment(), "Home", true);
			}
			else {
				Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
			}
		}
	}
}