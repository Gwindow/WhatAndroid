package what.whatandroid.torrentgroup;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import what.whatandroid.R;
import what.whatandroid.settings.SettingsActivity;

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
				File torrent = new File(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
				String torrentDir = SettingsActivity.torrentDownloadPath(context);
				if (!torrentDir.isEmpty()){
					File oldTorrent = torrent;
					torrent = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
						"/" + torrentDir + "/" + oldTorrent.getName());
					try {
						FileUtils.copyFile(oldTorrent, torrent);
					}
					catch (IOException e){
						Toast.makeText(context, "Failed to move torrent to " + torrentDir, Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						//Switch back to the original file download if we couldn't move it
						torrent = oldTorrent;
					}
					//Delete the old torrent file if we moved it successfully
					if (!torrent.equals(oldTorrent)){
						oldTorrent.deleteOnExit();
					}
				}

				Uri uri = Uri.fromFile(torrent);
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
