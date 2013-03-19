package what.gui;

import what.settings.Settings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.cli.Utils;
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
	private final Number size;
	private final Number snatches;
	private final Number seeders;
	private final Number leechers;
	private final Context context;

	/**
	 * @todo descriptions
	 */
	public DownloadDialog(Context context, Number torrentId, String downloadUrl,
			Number size, Number snatches, Number seeders, Number leechers) {
		super(context);
		this.context = context;
		this.downloadUrl = downloadUrl;
		this.torrentId = torrentId.intValue();

		this.leechers = leechers;
		this.seeders = seeders;
		this.snatches = snatches;
		this.size = size;

		init();
	}

	private void init() {
		setTitle(DIALOG_TITLE);
		setCancelable(true);
		setPositiveButton(DOWNLOAD_BUTTON, this);
		setNegativeButton(PYWA_BUTTON, this);

		LayoutInflater inflater =
				(LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout =
				(RelativeLayout) inflater.inflate(R.layout.download_dialog, null);

		TextView size_view = (TextView) layout.findViewById(R.id.size);
		if (size != null) {
			size_view.setText("Size: " + Utils.toHumanReadableSize(size.longValue()));
		}

		TextView snatches_view = (TextView) layout.findViewById(R.id.snatches);
		if (snatches != null) {
			snatches_view.setText("Snatches: " + snatches);
		}

		TextView seeders_view = (TextView) layout.findViewById(R.id.seeders);
		if (seeders != null) {
			seeders_view.setText("Seeders: " + seeders);
		}

		TextView leechers_view = (TextView) layout.findViewById(R.id.leechers);
		if (leechers != null) {
			leechers_view.setText("Leechers: " + leechers);
		}

		setView(layout);

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
		context.startActivity(intent);
	}

	private void pyWA() {
		String host = Settings.getHostPreference();
		String port = Settings.getPortPreference();
		String password = Settings.getPasswordPreference();
		if (host.length() > 0 && port.length() > 0 && password.length() > 0) {
			String pyWaUrl =
					host + ":" + port + "/dl.pywa?pass=" + password + "&site=whatcd&id="
							+ torrentId;
			try {
                /*
                TODO: this is why send to pywa doesn't work, we can't call MySoup.scrape directly here
                because it will run on the main thread, it needs to be an async task
                that will then make the appropriate toast when it completes/fails
                 */

				MySoup.scrapeOther(pyWaUrl);
				Toast.makeText(context, "Torrent sent", Toast.LENGTH_SHORT).show();
			} catch (CouldNotLoadException e) {
				Toast.makeText(context, "Could not send torrent", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(context, "Fill out pyWA information in Settings",
					Toast.LENGTH_LONG).show();
		}
	}

}
