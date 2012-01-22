package what.login;

import what.gui.MyActivity;
import what.gui.R;
import what.home.HomeActivity;
import what.settings.Settings;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import api.soup.MySoup;
import api.util.CouldNotLoadException;
import api.whatstatus.WhatStatus;

public class WhatAndroidActivity extends MyActivity implements OnClickListener {
	private final static String SITE = "http://67.183.192.159/";
	// TODO fill out
	private final static String UPDATE_SITE = "";
	private TextView username, password;
	private CheckBox ssl, rememberme;
	private Button login;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.login);

		MySoup.setSite(SITE);

		Settings.init(this);

		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		rememberme = (CheckBox) this.findViewById(R.id.remember_checkbox);
		rememberme.setOnClickListener(this);
		ssl = (CheckBox) this.findViewById(R.id.ssl_checkbox);
		ssl.setOnClickListener(this);
		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

		tryAutoLogin();
	}

	private void tryAutoLogin() {
		if (Settings.getRememberMe()) {
			new Login().execute(new String[] { Settings.getUsername(), Settings.getPassword() });
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == login.getId()) {
			if (username.length() > 0 && password.length() > 0)
				new Login().execute(new String[] { username.getText().toString().trim(), password.getText().toString() });

		}

	}

	private class Login extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(WhatAndroidActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Logging in...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			if (ssl.isChecked()) {
				MySoup.enableSSL();
				Settings.saveSSL(true);
			}
			if (rememberme.isChecked()) {
				Settings.saveUsername(username);
				Settings.savePassword(password);
			}
			Settings.commit();
			try {
				MySoup.login("login.php", username, password);
				return true;
			} catch (CouldNotLoadException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				unlockScreenRotation();
				intent = new Intent(WhatAndroidActivity.this, HomeActivity.class);
				startActivity(intent);
			}
			if (status == false) {
				new AlertDialog.Builder(WhatAndroidActivity.this).setTitle("Could not login")
						.setMessage("Check your username/password, a timeout occured, or the site is down")
						.setPositiveButton("Close", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								unlockScreenRotation();
							}
						}).setNegativeButton("Open WhatStatus", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								unlockScreenRotation();
								intent = new Intent(WhatAndroidActivity.this, WhatStatus.class);
								startActivity(intent);
							}

						}).show();
			}
		}
	}

}
