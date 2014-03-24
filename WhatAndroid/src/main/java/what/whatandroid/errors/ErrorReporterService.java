package what.whatandroid.errors;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import org.apache.commons.io.IOUtils;
import what.whatandroid.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

/**
 * Service to search the filesystem for existing error reports and push a notification to
 * email the reports to the devs
 */
public class ErrorReporterService extends IntentService {

	public ErrorReporterService(){
		super("WhatAndroid-ErrorReporterService");
	}

	/**
	 * Check the system for an crash reports and build up a report containing all of them
	 * then push a notification to the user that there are reports available to send
	 */
	@Override
	protected void onHandleIntent(Intent intent){
		StringBuilder reports = findReports();
		if (reports.length() != 0){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
			builder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Crash Reports Found")
				.setContentText("Click to send reports to the devs")
				.setAutoCancel(true);

			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "whatcdandroid@gmail.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WhatAndroid Crash Report");
			emailIntent.putExtra(Intent.EXTRA_TEXT, reports.toString());
			emailIntent = Intent.createChooser(emailIntent, "Send crash reports");
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, emailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendingIntent);

			NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(0, builder.build());
		}
	}

	/**
	 * Search the system for error reports and delete the crash files
	 *
	 * @return the built string containing all found error reports
	 */
	private StringBuilder findReports(){
		StringBuilder reports = new StringBuilder();
		if (getFilesDir() == null){
			return reports;
		}
		try {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename){
					return filename.endsWith(".report");
				}
			};
			for (File f : getFilesDir().listFiles(filter)){
				BufferedReader reader = new BufferedReader(new FileReader(f));
				reports.append(IOUtils.toString(reader))
					.append("\n\n");
				//Clean up error reports
				if (!f.delete()){
					System.err.println("Failed to delete file: " + f.getName());
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return reports;
	}
}
