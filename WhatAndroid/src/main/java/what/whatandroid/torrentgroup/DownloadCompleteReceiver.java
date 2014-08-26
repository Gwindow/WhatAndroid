package what.whatandroid.torrentgroup;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import what.whatandroid.R;

/**
 * Receiver to listen for when a torrent downloaded to
 * the phone has finished downloading
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
	private static final Pattern torrentId = Pattern.compile("id=(\\d+)");

	@Override
	public void onReceive(Context context, Intent intent){
		context.unregisterReceiver(this);
		long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		int reqId = (int)System.currentTimeMillis();
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(id);
		Cursor cursor = ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE)).query(query);
		if (cursor.moveToFirst()){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL){
				String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
				builder.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("File Download Complete")
					.setContentText("Download of " + title + " completed")
					.setAutoCancel(true);

				//We can't use COLUMN_LOCAL_URI like sane people because Transdroid is too dumb to handle
				//content://downloads/my_downloads/#### since it can only figure out content://downloads/all_downloads/####
				Uri uri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))));
				Intent view = new Intent(Intent.ACTION_VIEW, uri);
				view.setDataAndType(uri, "application/x-bittorrent");
				view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, reqId + 1000, view, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pendingIntent);
			}
			else {
				String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
				builder.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("File Download Failed")
					.setContentText("Download of " + title + " failed\n" +
						cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)))
					.setAutoCancel(true);

				Matcher matcher = torrentId.matcher(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI)));
				int torrent = 0;
				if (matcher.find()){
					torrent = Integer.parseInt(matcher.group(1));
				}
				Intent view = new Intent(context, TorrentGroupActivity.class);
				view.putExtra(TorrentGroupActivity.TORRENT_ID, torrent);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, reqId, view, 0);
				builder.setContentIntent(pendingIntent);
			}
			NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(reqId, builder.build());
		}
		cursor.close();
	}
}
