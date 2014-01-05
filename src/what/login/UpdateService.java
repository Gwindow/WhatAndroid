package what.login;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import api.son.MySon;
import what.gui.R;
import what.settings.Settings;
import what.util.GitRelease;
import what.util.VersionNumber;

/**
 * Update checker service, checks the Github releases API
 * for the project and gives the user a notification if a new
 * version is available for download
 */
public class UpdateService extends IntentService {
	/** The Github releases API endpoint for the project */
	private static final String GH_RELEASES = "https://api.github.com/repos/Gwindow/WhatAndroid/releases";

	public UpdateService(){
		super("WhatAndroidUpdateService");
	}

	/**
	 * The only intent we handle is to check Github for a newer version,
	 * so that's all we do
	 * @param intent the intent, essentially ignored
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		GitRelease latest = null;
		try {
			GitRelease[] releases = (GitRelease[]) MySon.toObjectOther(GH_RELEASES, GitRelease[].class);
			if (releases != null){
				VersionNumber current = getInstalledVersion();
				for (GitRelease gr : releases){
					//Ignore drafts entirely and pre-release builds if we don't want dev builds
					if (gr.isDraft() || (gr.isPrerelease() && !Settings.useDevBuilds())){
						continue;
					}
					//Releases are in chronological order so the first higher release we encounter is the latest
					if (current == null || gr.getVersionNumber().isHigher(current)){
						latest = gr;
						break;
					}
					//If we hit a release lower numbered than our current one we know there's no new release
					if (current.isHigher(gr.getVersionNumber())){
						break;
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		//If there's a new release make a notification that will open the release page on Github
		if (latest != null){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("New version of WhatAndroid available!")
				.setContentText("Version " + latest.getVersionNumber().toString() + " is available on Github")
				.setAutoCancel(true);

			Intent viewRelease = new Intent(Intent.ACTION_VIEW);
			viewRelease.setData(Uri.parse(latest.getHtmlUrl()));
			viewRelease.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, viewRelease, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendingIntent);

			NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(0, builder.build());
		}
	}

	/**
	 * Get the version number of the currently installed app
	 * @return the apps version number, or null if something went wrong
	 */
	private VersionNumber getInstalledVersion(){
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			return new VersionNumber(manager.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
