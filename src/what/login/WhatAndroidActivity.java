package what.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import what.cache.ImageCache;
import what.forum.section.SectionActivity2;
import what.gui.MyActivity;
import what.gui.R;
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
import android.view.LayoutInflater;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class WhatAndroidActivity extends MyActivity implements OnClickListener {
	// TODO remove
	private static final double VERSION = 0.50;
	private static String SITE = "";
	private static boolean USE_SSL = false;
	private final static String UPDATE_SITE = "http://gwindow.github.com/WhatAndroid/index.html";

	private static final String MENU_ITEM_DEVELOPER = "Developer";
	private static final String MENU_ITEM_SCANNER = "Quick Scanner";
	private static final String MENU_ITEM_WHAT_STATUS = "WhatStatus";
	public static double INSTALLED_VERSION;
	private TextView username, password;
	private CheckBox rememberme;
	private Button login;
	private Intent intent;
	private Updater updater;
	private boolean hasUpdates;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		super.setContentView(R.layout.login, false);
		tryAutoLogin();
	}

	@Override
	public void init() {
		INSTALLED_VERSION = getInstalledVersion();
		MySoup.setSite(SITE, USE_SSL);
		MySon.setDebugEnabled(Settings.getDebugPreference());
		try {
			checkForUpdates();
		} catch (CouldNotLoadException e) {
			e.printStackTrace();
		}
		ImageCache.init(this);
	}

	@Override
	public void load() {
		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		rememberme = (CheckBox) this.findViewById(R.id.remember_checkbox);
		rememberme.setOnClickListener(this);

		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

		username.setText("gazelle");
		password.setText("123456");
	}

	@Override
	public void actionbar() {
		getSupportActionBar().setTitle("The What.CD Android App");
	}

	@Override
	public void prepare() {
		enableGestures(false);
		rememberme.setChecked(Settings.getRememberMe());
		tryAutoLogin();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_ITEM_SCANNER);
		menu.add(MENU_ITEM_WHAT_STATUS);
		menu.add(MENU_ITEM_DEVELOPER);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().toString().equals(MENU_ITEM_SCANNER)) {
			intent = new Intent(WhatAndroidActivity.this, what.barcode.QuickScannerActivity.class);
			startActivity(intent);
		}
		if (item.getTitle().toString().equals(MENU_ITEM_WHAT_STATUS)) {
			intent = new Intent(WhatAndroidActivity.this, what.status.WhatStatusActivity.class);
			startActivity(intent);
		}
		if (item.getTitle().toString().equals(MENU_ITEM_DEVELOPER)) {
			developerOverride();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void developerOverride() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Gazelle Site URL");

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.developer_override, null);
		final EditText input = (EditText) view.findViewById(R.id.url_field);
		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.ssl_checkbox);
		alert.setView(view);
		alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String url = input.getText().toString();
				if (url.length() > 0) {
					SITE = url;
					USE_SSL = checkbox.isChecked();
					MySoup.setSite(SITE, USE_SSL);
					Toast.makeText(WhatAndroidActivity.this, MySoup.getSite(), Toast.LENGTH_LONG).show();

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
			dialog.setPositiveButton(message.getC(), null);
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
				intent = new Intent(WhatAndroidActivity.this, SectionActivity2.class);
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

}
