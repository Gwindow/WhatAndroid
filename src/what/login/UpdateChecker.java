package what.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import api.son.MySon;
import what.settings.Settings;
import what.util.GitRelease;
import what.util.VersionNumber;

/**
 * @author Gwindow
 * @since Jun 5, 2012 7:02:12 PM
 */
public class UpdateChecker {
	//The url for the app's releases page
	private static final String GH_RELEASES = "https://api.github.com/repos/Gwindow/WhatAndroid/releases";
	private Context context;

	public UpdateChecker(Context context) {
		this.context = context;
	}

	/**
	 * Run the Update checker async task, an Alert dialog will be created
	 * if a new version is available
	 */
	public void checkForUpdates() {
		new CheckUpdates().execute();
	}

	/**
	 * Show an Alert Dialog to see if the user wants to view the information and downloads for the latest
	 * release of the app on our Github releases page
	 * @param release The latest release of the app
	 */
	private void showUpdates(final GitRelease release) {
		//If there's a new release available prompt the user if they'd like to view it on the Github release page
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("New Version Available");
		alert.setMessage("Version " + release.getVersionNumber() + " has been released, would you like to update?");
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				Intent viewRelease = new Intent(Intent.ACTION_VIEW);
				viewRelease.setData(Uri.parse(release.getHtmlUrl()));
				context.startActivity(viewRelease);
			}
		});
		alert.setNegativeButton("Not Now", null);
		alert.setCancelable(true);
		alert.create().show();
	}

	private VersionNumber getInstalledVersion() {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return new VersionNumber(manager.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
	}

	/**
	 * An AsyncTask to check the Github releases page for new releases, will call
	 * showUpdates if a new release is available
	 */
	private class CheckUpdates extends AsyncTask<Void, Void, Boolean> {
		//The latest release of the app, or null if we're on the latest release
		private GitRelease latestRelease;

		@Override
		protected Boolean doInBackground(Void... params){
			//Catch any errors that may occur and interpret them as no updates available
			try {
				GitRelease[] releases = (GitRelease[])MySon.toObjectOther(GH_RELEASES, GitRelease[].class);
				if (releases != null){
					VersionNumber currentVer = getInstalledVersion();

					for (GitRelease gr : releases){
						//Ignore draft builds, also pre-releases if we don't want dev builds
						if (gr.isDraft() || (gr.isPrerelease() && !Settings.useDevBuilds()))
							continue;
						//If there's a new release
						if (currentVer == null || gr.getVersionNumber().isHigher(currentVer)){
							latestRelease = gr;
							return true;
						}
						//If the current version is the same as that on Github then we're on the latest and there's no
						//point against any more releases, since the releases are in chronological order it's
						//ok to break at the first release encountered with a <= version number.
						else
							return false;
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			//If there's a new release show the alert
			if (status) {
				showUpdates(latestRelease);
			}
		}
	}
}
