//Arbitrary location for the todo list of major things
//TODO 2) pages for threads and last read
//TODO 3) serializible subscriptions/new threads
//TODO 4) GUI recode
//TODO 5) A mainpage that displays dynamic feed of forum
//TODO 6) Jump to last read post
//TODO 7) music searching
//TODO 8) downloading/sending to seedbox
//TODO 9) User Settings
//TODO 10) IRC client

// Gui to-dos

//TODO GUI off by one) Try Catch statements for all communication with what.cd 
//TODO GUI -1) create new thread button and activity
//TODO GUI 38422) rotation
//TODO GUI 55) Music Browsing
//TODO GUI imaginary number) Fix changing pages in threads!!!
//TODO GUI 0) jump to last read arrow
//TODO GUI 1) Make sure all loading processes are threaded
//TODO GUI 4) Finish converting all layouts to XML
//TODO GUI 5) Finish revamping each activity's GUI
//TODO GUI 6) Add landing page with "what's new"
//TODO GUI 7) Create widget GUI
//TODO GUI 8) Convert all Activities to fragments
//TODO GUI 9) Create layouts for tablets
//TODO GUI 10) Move most functions from menus onto screen for better UX
//TODO GUI 11) Have post activity save info so no need to reload on orientation switch
//TODO GUI 12) Have post activity reload itself on page change instead of loading new activity

//IRC TODO
//TODO IRC 0) something functional
//TODO IRC 1) channel list
//TODO IRC 2) user list
//TODO IRC 3) private messages
//TODO IRC 4) multiple channels
//TODO IRC 5) drone identification
//TODO IRC 6) More Commands
//TODO IRC 7) Topic view
//TODO IRC 8) SSL

package what.login;

import java.io.IOException;

import what.gui.MyActivity;
import what.gui.Notification;
import what.gui.R;
import what.gui.ReportSender;
import what.settings.Settings;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

/**
 * Login screen
 * 
 * @author Tim
 * 
 */
public class WhatAndroidActivity extends MyActivity implements OnClickListener, OnEditorActionListener {
	private String VERSION;
	private String SITE = "http://timmikeladze.github.com/whatAndroid/index.html";
	TextView username;
	TextView password;
	Button login;
	CheckBox rememberCheckbox;
	CheckBox passwordCheckbox;
	CheckBox sslCheckbox;
	Notification notification = new Notification();

	Settings settings;

	private String usernameString;
	private String passwordString;

	/**
	 * Called when the activity is first created.
	 * */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);
		MySoup.setSite("http://192.168.1.147:8080/");
		// MySoup.setSite("http://173.250.180.148:8080/");
		setVersionName();
		try {
			checkForUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// initialize the settings writer, should only be done once
		Settings.init(this);

		// Set UI component references
		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		password.setOnEditorActionListener(this);
		rememberCheckbox = (CheckBox) this.findViewById(R.id.remember_checkbox);
		rememberCheckbox.setOnClickListener(this);
		passwordCheckbox = (CheckBox) this.findViewById(R.id.rememberpassword_checkbox);
		passwordCheckbox.setOnClickListener(this);

		sslCheckbox = (CheckBox) this.findViewById(R.id.ssl_checkbox);
		sslCheckbox.setOnClickListener(this);
		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

		String savedUsername = Settings.getUsername();
		String savedPassword = Settings.getPassword();
		boolean useSSL = Settings.getSSL();
		if (!savedUsername.equals("")) {
			username.setText(savedUsername);
			rememberCheckbox.setChecked(true);
		}
		if (!savedPassword.equals("")) {
			password.setText(savedPassword);
			passwordCheckbox.setChecked(true);
		}

		if (useSSL) {
			sslCheckbox.setChecked(true);
		}

		if (!savedUsername.equals("") && !savedPassword.equals("")) {
			try {
				login();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		username.setText("gazelle");
		password.setText("123456");

		/* if (settings.getString("sessionId", null) != null) { // Resume the session // TODO fix // resume(); } */
	}

	/**
	 * Set the version name from the manifest file
	 */
	private void setVersionName() {
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			VERSION = manager.versionName;
		} catch (NameNotFoundException e) {
			VERSION = "0";
			e.printStackTrace();
		}
	}

	/**
	 * Check if update exists by comparing versions
	 */
	private void checkForUpdate() {
		double updateversion = Double.parseDouble(MySoup.getUpdateVersion(SITE));
		double currentversion = Double.parseDouble(VERSION);
		if (updateversion > currentversion) {
			displayAlert("", "Update available, would you like to install it?", this);
		}
	}

	/**
	 * Open the download link to the update
	 */
	private void openUpdate() {
		String url = MySoup.getUpdateLink(SITE);
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
		finish();
	}

	public void displayAlert(String title, String message, Context context) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						openUpdate();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).show();
	}

	/**
	 * Login to what.cd
	 * 
	 * @throws IOException
	 */
	private void login() throws IOException {
		// Save username
		if (rememberCheckbox.isChecked()) {
			Settings.saveUsername(username.getText().toString());
		} else {
			Settings.saveUsername("");
		}

		// Save password
		if (passwordCheckbox.isChecked()) {
			Settings.savePassword(password.getText().toString());
		} else {
			Settings.savePassword("");
		}

		// Save SSL State
		Settings.saveSSL(sslCheckbox.isChecked());
		// Commit settings
		Settings.commit();

		usernameString = username.getText().toString();
		// trim whitespace on both ends very important!
		usernameString = usernameString.trim();
		passwordString = password.getText().toString();

		@SuppressWarnings("unused")
		ProgressDialog dialog = new ProgressDialog(this);

		Thread loadingThread = new Thread() {
			ProgressDialog dialog = new ProgressDialog(WhatAndroidActivity.this);
			String loginURL = "login.php";

			@Override
			public void run() {
				WhatAndroidActivity.this.lockScreenRotation();
				// Display the progress dialog
				loginHandler.sendEmptyMessage(1);
				// Enable SSL if needed
				if (sslCheckbox.isChecked()) {
					MySoup.enableSSL();
				}

				// Do the log in
				try {
					MySoup.login(loginURL, usernameString, passwordString);
					if (MySoup.isLoggedIn()) {
						// Save sessionId and authKey
						Settings.saveSessionId(MySoup.getSessionId());
						Settings.saveAuthKey(MySoup.getAuthKey());
						Settings.commit();
						// Manager.createForum("what.cd forum");
						// Manager.createSubscriptions("Subscriptions");
						loginHandler.sendEmptyMessage(2);
						// Start the next activity
					} else {
						// Display the error message
						loginHandler.sendEmptyMessage(4);
					}
				} catch (CouldNotLoadException e) {
					loginHandler.sendEmptyMessage(3);
					e.printStackTrace();
				}
			}

			private Handler loginHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						dialog.setIndeterminate(true);
						dialog.setMessage(getString(R.string.loggingin));
						dialog.show();
					} else if (msg.what == 2) {
						dialog.dismiss();
						WhatAndroidActivity.this.unlockScreenRotation();
						Intent intent = new Intent(WhatAndroidActivity.this, what.home.HomeActivity.class);
						// Intent intent = new Intent(WhatAndroidActivity.this, what.search.UserSearchActivity.class);
						startActivity(intent);
					} else if (msg.what == 3) {
						dialog.dismiss();
						notification.displayError("Error", "Login failed, wrong username/password or a timeout, try again",
								WhatAndroidActivity.this);
					} else if (msg.what == 4) {
						dialog.dismiss();
						notification.displayError("Error", "MySoup.isLoggedIn() == false", WhatAndroidActivity.this);
					}
				}
			};
		};
		loadingThread.start();
	}

	/**
	 * Doesn't work. TODO: Fix.
	 */
	@SuppressWarnings("unused")
	private void resume() {
		if ((Settings.getSessionId() == null) || (Settings.getSessionId() == ""))
			return;
		else {
			// MySoup.setAuthKey(Settings.getSessionId());
		}
		// Do the log in
		Thread loadingThread = new Thread() {
			ProgressDialog dialog = new ProgressDialog(WhatAndroidActivity.this);

			@Override
			public void run() {
				// Display the progress dialog
				loginHandler.sendEmptyMessage(1);
				// Enable SSL if needed
				if (sslCheckbox.isChecked()) {
					MySoup.enableSSL();
				}
				// Resume the session
				MySoup.setSessionId(Settings.getSessionId());
				// MySoup.setAuthKey(Settings.getAuthKey());
				if (MySoup.isLoggedIn()) {
					/* try { // Manager.createForum("what.cd forum"); // Manager.createSubscriptions("subscriptions"); }
					 * catch (CouldNotLoadException e) { e.printStackTrace(); } */
					// Start the next activity
					loginHandler.sendEmptyMessage(2);
				} else {
					// Display the error message
					loginHandler.sendEmptyMessage(3);
				}
			}

			private Handler loginHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						dialog.setIndeterminate(true);
						dialog.setMessage(getString(R.string.resume));
						dialog.show();
					} else if (msg.what == 2) {
						dialog.dismiss();
						// Intent intent = new Intent(WhatAndroidActivity.this, what.forum.SectionListActivity.class);
						// startActivity(intent);
					} else if (msg.what == 3) {
						dialog.dismiss();
						notification.displayError("Error", "Login failed, wrong username/password or a timeout, try again",
								WhatAndroidActivity.this);
					}
				}
			};
		};
		loadingThread.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			try {
				if (((username.getText().toString().length() != 0) && (password.getText().toString().length() != 0))
						|| MySoup.isLoggedIn()) {
					login();
				} else {
					Toast.makeText(this, "You need to enter a username and password!", Toast.LENGTH_LONG).show();
				}
			} catch (IOException e) {
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loginmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.quitItem:
			closeOptionsMenu();
			break;
		case R.id.statusItem:
			Intent intent = new Intent(this, what.status.StatusActivity.class);
			startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void openOptionsMenu() {
		super.openOptionsMenu();
	}

	@Override
	public void closeOptionsMenu() {
		super.closeOptionsMenu();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			login.performClick();
		}
		return true;
	}
}
