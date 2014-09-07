package what.whatandroid.updater;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import api.son.MySon;
import what.whatandroid.R;

/**
 * Update checker service, currently checks the github pages for
 * the repo to look for new alpha releases
 */
public class UpdateService extends IntentService {
	/**
	 * The github releases API endpoint for the project
	 */
	private static final String RELEASES_PAGE = "https://api.github.com/repos/Gwindow/WhatAndroid/releases";

	public UpdateService(){
		super("WhatAndroid-UpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		try {
			GitRelease[] releases = (GitRelease[])MySon.toObjectOther(RELEASES_PAGE, GitRelease[].class);
			if (releases != null){
				VersionNumber current = getVersionNumber();
				for (GitRelease gr : releases){
					System.out.println("Looking at release " + gr);
					//TODO: Check if it's a pre-release and if we're subscribed to the testing channel
					//if we're not subscribed to testing channel we should ignore pre-releases too
					if (gr.isDraft()){
						continue;
					}
					//Releases are in chronological order so the first higher release we encounter is the latest
					//for the same reason if we hit a release <= our version then we know there's no new build
					if (current == null || gr.getVersionNumber().isHigher(current)){
						notifyNewRelease(gr);
						return;
					}
					else {
						return;
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void notifyNewRelease(GitRelease release){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("WhatAndroid Update Available")
			.setContentText("Version " + release.getVersionNumber() + " is available")
			.setAutoCancel(true);

		Intent download = new Intent(Intent.ACTION_VIEW, Uri.parse(release.getHtmlUrl()));
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, download, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);

		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, builder.build());
	}

	private VersionNumber getVersionNumber(){
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			return new VersionNumber(pi.versionName);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
