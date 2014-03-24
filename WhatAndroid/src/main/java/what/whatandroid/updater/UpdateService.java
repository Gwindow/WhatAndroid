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
	 * The github pages webpage that we check
	 */
	private static final String RELEASES_PAGE = "http://gwindow.github.io/WhatAndroid/alpha_updater.html";

	public UpdateService(){
		super("WhatAndroid-UpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		try {
			AlphaRelease release = (AlphaRelease)MySon.toObjectOther(RELEASES_PAGE, AlphaRelease.class);
			if (release != null){
				System.out.println("Found release: " + release);
				VersionNumber current = getVersionNumber();
				if (current == null || release.getVersionNumber().isHigher(current)){
					notifyNewRelease(release);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void notifyNewRelease(AlphaRelease release){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("New WhatAndroid alpha build")
			.setContentText("Alpha version: " + release.getVersionNumber() + " is available")
			.setAutoCancel(true);

		Intent download = new Intent(Intent.ACTION_VIEW, Uri.parse(release.getUrl()));
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, download, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);

		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
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
