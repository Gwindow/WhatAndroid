package what.torrents.torrents;

import what.settings.Settings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

/**
 * @author Gwindow
 * @since Jun 3, 2012 9:43:25 AM
 */
public class DownloadDialog extends AlertDialog.Builder implements OnClickListener {
	private static final String DIALOG_TITLE = "Download...";
	private static final String DOWNLOAD_BUTTON = "Download Torrent";
	private static final String PYWA_BUTTON = "Send to pyWA";
	private final int torrentId;
	private final String downloadUrl;

	/**
	 * @param arg0
	 */
	public DownloadDialog(Context context, int torrentId, String downloadUrl) {
		super(context);
		this.downloadUrl = downloadUrl;
		this.torrentId = torrentId;

		setTitle(DIALOG_TITLE);
		setCancelable(true);
		setPositiveButton(DOWNLOAD_BUTTON, this);
		setNegativeButton(PYWA_BUTTON, this);
		create();
		show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			download();
		}
		if (which == AlertDialog.BUTTON_NEGATIVE) {
			pyWA();
		}
	}

	private void download() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
		getContext().startActivity(intent);
	}

	private void pyWA() {
		String host = Settings.getHostPreference();
		String port = Settings.getPortPreference();
		String password = Settings.getPasswordPreference();
		if ((host.length() > 0) && (port.length() > 0) && (password.length() > 0)) {
			String pyWaUrl = host + ":" + port + "/dl.pywa?pass=" + password + "&site=whatcd&id=" + torrentId;
			try {
				MySoup.scrapeOther(pyWaUrl);
				Toast.makeText(getContext(), "Torrent sent", Toast.LENGTH_SHORT).show();
			} catch (CouldNotLoadException e) {
				Toast.makeText(getContext(), "Could not send torrent", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getContext(), "Fill out pyWA information in Settings", Toast.LENGTH_LONG).show();
		}
	}

}
