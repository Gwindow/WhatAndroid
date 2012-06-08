package what.login;

import what.cache.ImageCache;
import what.forum.ForumActivity;
import what.gui.MenuItems;
import what.gui.MyActivity2;
import what.gui.R;
import what.settings.Settings;
import what.status.WhatStatusActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import api.son.MySon;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * The Login activity.
 * 
 * @author Gwindow
 * @since Jun 5, 2012 9:46:17 PM
 */
public class WhatAndroidActivity extends MyActivity2 implements OnClickListener {

	/** The Constant REDIRECT_CLASS. */
	private static final Class<?> REDIRECT_CLASS = ForumActivity.class;

	/** The Constant SITE. */
	private static final String SITE = "ssl.what.cd";

	/** The Constant USE_SSL. */
	private static final boolean USE_SSL = true;

	private TextView username, password;

	private CheckBox rememberme;

	/** The login. */
	private Button login;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, com.actionbarsherlock.R.style.DarkTheme);
		super.setContentView(R.layout.login, false);
	}

	@Override
	public void init() {
		MySoup.setSite(SITE, USE_SSL);
		MySon.setDebugEnabled(Settings.getDebugPreference());
		MenuItems.init();
		ImageCache.init(this);
		new UpdateChecker(this).checkForUpdates();
	}

	@Override
	public void load() {
		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		rememberme = (CheckBox) this.findViewById(R.id.remember_checkbox);
		rememberme.setOnClickListener(this);

		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

		rememberme.setChecked(Settings.getRememberMe());

		username.setText("");
		password.setText("");
	}

	@Override
	public void actionbar() {
		setActionBarTitle("The What.CD Android App");
	}

	@Override
	public void prepare() {
		tryAutoLogin();
	}

	/**
	 * Try auto login.
	 */
	private void tryAutoLogin() {
		if (Settings.getRememberMe()) {
			username.setText(Settings.getUsername());
			password.setText(Settings.getPassword());
			new Login().execute(Settings.getUsername(), Settings.getPassword());
		}
	}

	/**
	 * On click.
	 * 
	 * @param v
	 *            the view
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == login.getId()) {
			if ((username.length() > 0) && (password.length() > 0)) {
				new Login().execute(username.getText().toString().trim(), password.getText().toString());
			} else {
				Toast.makeText(this, "Fill out login form", Toast.LENGTH_LONG).show();
			}
		}

	}

	/**
	 * On create options menu.
	 * 
	 * @param menu
	 *            the menu
	 * @return true, if successful
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	/**
	 * On options item selected.
	 * 
	 * @param item
	 *            the item
	 * @return true, if successful
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.scanner_item:
				intent = new Intent(this, what.barcode.QuickScannerActivity.class);
				startActivity(intent);
				break;
			case R.id.status_item:
				intent = new Intent(this, what.status.WhatStatusActivity.class);
				startActivity(intent);
				break;
			case R.id.override_item:
				developerOverride();
				break;
			default:
				break;
		}
		return false;
	}

	/**
	 * Developer override.
	 */
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
					MySoup.setSite(url, checkbox.isChecked());
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

	/**
	 * The Class Login.
	 * 
	 * @author Gwindow
	 * @since Jun 5, 2012 9:46:17 PM
	 */
	private class Login extends AsyncTask<String, Void, Boolean> {

		/** The dialog. */
		private ProgressDialog dialog;

		/**
		 * On pre execute.
		 */
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(WhatAndroidActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Logging in...");
			dialog.show();
		}

		/**
		 * Do in background.
		 * 
		 * @param params
		 *            the params
		 * @return the boolean
		 */
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
				return false;
			}
		}

		/**
		 * On post execute.
		 * 
		 * @param status
		 *            the status
		 */
		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			unlockScreenRotation();
			if (status) {
				Settings.saveUserId(MySoup.getUserId());
				Intent intent = new Intent(WhatAndroidActivity.this, REDIRECT_CLASS);
				startActivity(intent);
			} else {
				new AlertDialog.Builder(WhatAndroidActivity.this).setTitle("Could not login")
						.setMessage("Check your username/password, a timeout occured, or the site is down")
						.setPositiveButton("Close", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							}
						}).setNegativeButton("Open WhatStatus", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								unlockScreenRotation();
								Intent intent = new Intent(WhatAndroidActivity.this, WhatStatusActivity.class);
								startActivity(intent);
							}

						}).show();
			}
		}
	}

}
