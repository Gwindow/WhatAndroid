package what.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import what.settings.Settings;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.WebView;
import android.widget.Toast;
import api.util.CouldNotLoadException;
import api.util.Triple;
import api.util.Updater;

/**
 * @author Gwindow
 * @since Jun 5, 2012 7:02:12 PM
 */
public class UpdateChecker {
	public static final double VERSION = 0.50;
	private static final String UPDATE_SITE = "http://gwindow.github.com/WhatAndroid/index.html";
	private Updater updater;
	private Context context;

	public UpdateChecker(Context context) {
		this.context = context;
		updater = new Updater(UPDATE_SITE);
	}

	public void checkForUpdates() {
		new CheckUpdates().execute();
	}

	private void showUpdates() {
		final Triple<String, String, String> message = updater.getMessage();
		final Double version = updater.getVersion();
		final String link = updater.getDownloadLink();
		if (version > getInstalledVersion()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Update available");
			alert.setMessage("Version " + version + " has been released, would you like to update?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new UpdateDownloader().execute(link);
				}
			});
			alert.setNegativeButton("No", null);
			alert.setCancelable(true);
			alert.create().show();
		}
		if (message.getB().hashCode() != Settings.getMessageHashCode()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(message.getA());
			WebView webView = new WebView(context);
			webView.loadData(message.getB(), "text/html", "UTF-8");
			dialog.setView(webView);
			dialog.setPositiveButton(message.getC(), null);
			dialog.setCancelable(true);
			dialog.create().show();
			Settings.saveMessageHashCode(message.getB().hashCode());
			Settings.commit();
		}
	}

	private double getInstalledVersion() {
		int versionCode;
		String versionName;
		double installedVersion = 0;
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
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

	private class CheckUpdates extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean status = false;
			try {
				updater.checkForUpdates();
				status = true;
			} catch (CouldNotLoadException e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status) {
				showUpdates();
			}
		}
	}

	private class UpdateDownloader extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setIndeterminate(true);
			dialog.setMessage("Updating...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean status = false;
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
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "WhatAndroid.apk")),
						"application/vnd.android.package-archive");
				context.startActivity(intent);
			} else {
				Toast.makeText(context, "Update failed, install manually from http://bit.ly/git_wa_build/", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
}
