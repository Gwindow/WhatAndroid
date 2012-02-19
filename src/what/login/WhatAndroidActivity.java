package what.login;

import what.cache.ImageCache;
import what.gui.MyActivity;
import what.gui.R;
import what.home.HomeActivity;
import what.settings.Settings;
import what.status.WhatStatusActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import api.soup.MySoup;
import api.util.CouldNotLoadException;
import api.util.Triple;
import api.util.Updater;

public class WhatAndroidActivity extends MyActivity implements OnClickListener {
	// TODO remove
	private final static double VERSION = 0.17;
	private final static String SITE = "http://what.cd/";
	private final static String UPDATE_SITE = "https://raw.github.com/Gwindow/WhatAndroid/gh-pages/index.html";
	private static double INSTALLED_VERSION;
	private TextView username, password;
	private CheckBox ssl, rememberme;
	private Button login;
	private Intent intent;
	private Updater updater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.login);

		Settings.init(this);
		ImageCache.init();
		INSTALLED_VERSION = getInstalledVersion();
		MySoup.setSite(SITE);

		try {
			checkForUpdates();
		} catch (CouldNotLoadException e) {
			e.printStackTrace();
		}

		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		rememberme = (CheckBox) this.findViewById(R.id.remember_checkbox);
		rememberme.setChecked(Settings.getRememberMe());
		rememberme.setOnClickListener(this);
		ssl = (CheckBox) this.findViewById(R.id.ssl_checkbox);
		ssl.setOnClickListener(this);
		// TODO remove

		ssl.setVisibility(CheckBox.INVISIBLE);
		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

		tryAutoLogin();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	private void checkForUpdates() throws CouldNotLoadException {
		updater = new Updater(UPDATE_SITE);
		final Triple<String, String, String> message = updater.getMessage();
		final Double version = updater.getVersion();
		final String link = updater.getDownloadLink();
		if (version > INSTALLED_VERSION) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Update available");
			alert.setMessage("Version " + version + " has been released, would you like to update?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openUpdate(link);
				}
			});
			alert.setNegativeButton("No", null);
			alert.setCancelable(true);
			alert.create().show();
		}
		if (message.getB().hashCode() != Settings.getMessageHashCode()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(message.getA());
			dialog.setMessage(message.getB() + "\n\n" + message.getC());
			dialog.setPositiveButton("Okay", null);
			dialog.setCancelable(true);
			dialog.create().show();
			Settings.saveMessageHashCode(message.getB().hashCode());
			Settings.commit();
		}
	}

	private void openUpdate(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
		finish();
	}

	private double getInstalledVersion() {
		int versionCode;
		String versionName;
		double installedVersion = 0;
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionCode = manager.versionCode;
			versionName = manager.versionName;
			installedVersion = versionCode + Double.parseDouble(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// TODO remove
		// return installedVersion;
		return VERSION;
	}

	private void tryAutoLogin() {
		if (Settings.getRememberMe()) {
			username.setText(Settings.getUsername());
			password.setText(Settings.getPassword());

			new Login().execute(new String[] { Settings.getUsername(), Settings.getPassword() });
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == login.getId()) {
			if ((username.length() > 0) && (password.length() > 0)) {
				new Login().execute(new String[] { username.getText().toString().trim(), password.getText().toString() });
			} else {
				Toast.makeText(this, "Fill out login form", Toast.LENGTH_LONG).show();
			}
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
				Settings.saveRememberMe(true);
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
			unlockScreenRotation();
			if (status == true) {
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
								intent = new Intent(WhatAndroidActivity.this, WhatStatusActivity.class);
								startActivity(intent);
							}

						}).show();
			}
		}
	}

}
