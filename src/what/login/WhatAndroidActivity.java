package what.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import api.son.MySon;
import api.soup.MySoup;
import api.util.CouldNotLoadException;
import api.util.Triple;
import api.util.Updater;

public class WhatAndroidActivity extends MyActivity implements OnClickListener {
	// TODO remove
	private final static double VERSION = 0.32;
	private static String SITE = "ssl.what.cd";
	private final static String UPDATE_SITE = "https://raw.github.com/Gwindow/WhatAndroid/gh-pages/index.html";
	public static double INSTALLED_VERSION;
	private TextView username, password;
	private CheckBox ssl, rememberme;
	private Button login;
	private Intent intent;
	private Updater updater;
	private boolean hasUpdates;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.login, false);

		ImageCache.init(this);
		INSTALLED_VERSION = getInstalledVersion();
		MySoup.setSite(SITE);
		MySon.setDebugEnabled(Settings.getDebugPreference());

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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loginmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scannerItem:
			intent = new Intent(WhatAndroidActivity.this, what.barcode.QuickScannerActivity.class);
			startActivity(intent);
			break;
		case R.id.statusItem:
			intent = new Intent(WhatAndroidActivity.this, what.status.WhatStatusActivity.class);
			startActivity(intent);
			break;
		case R.id.overrideItem:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Gazelle Site URL");
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					String url = input.getText().toString();
					if (url.length() > 0) {
						SITE = url;
						MySoup.setSite(SITE);
					} else {
						Toast.makeText(WhatAndroidActivity.this, "URL not entered", Toast.LENGTH_LONG).show();
					}
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			alert.create().show();
			break;
		default:
			break;
		}
		return true;
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
					new Update().execute(link);
				}
			});
			alert.setNegativeButton("No", null);
			alert.setCancelable(true);
			alert.create().show();
			hasUpdates = true;
		}
		if (message.getB().hashCode() != Settings.getMessageHashCode()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(message.getA());
			WebView webView = new WebView(this);
			webView.loadData(message.getB(), "text/html", "UTF-8");
			dialog.setView(webView);
			// dialog.setMessage(message.getB() + "\n\n" + message.getC());
			dialog.setPositiveButton("Okay", null);
			dialog.setCancelable(true);
			dialog.create().show();
			Settings.saveMessageHashCode(message.getB().hashCode());
			Settings.commit();
			hasUpdates = true;
		}
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
			if (!hasUpdates) {
				new Login().execute(new String[] { Settings.getUsername(), Settings.getPassword() });
			}
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
				Settings.saveUserId(MySoup.getUserId());
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

	private class Update extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(WhatAndroidActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Updating...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean status;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();

				String PATH = Environment.getExternalStorageDirectory() + "/download/";
				File file = new File(PATH);
				file.mkdirs();
				File outputFile = new File(file, "WhatAndroid.apk");
				FileOutputStream fos = new FileOutputStream(outputFile);

				InputStream is = c.getInputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len1);
				}
				fos.close();
				is.close();

				status = true;
			} catch (IOException e) {
				status = false;
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			unlockScreenRotation();
			if (status == true) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "WhatAndroid.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
			}
			if (status == false) {
				Toast.makeText(WhatAndroidActivity.this, "Update failed, install manually from http://bit.ly/git_wa_build",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onMenuGesturePerformed() {
	}

	@Override
	public void onRefreshGesturePerformed() {

	}

	@Override
	public void onHomeGesturePerformed() {
	}

	@Override
	public void onRightGesturePerformed() {

	}

	@Override
	public void onLeftGesturePerformed() {

	}

	@Override
	public void onDownGesturePerformed() {
	}

	@Override
	public void onUpGesturePerformed() {

	}

}
