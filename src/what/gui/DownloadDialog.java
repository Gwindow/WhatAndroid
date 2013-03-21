package what.gui;

import android.os.AsyncTask;
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
import what.settings.Settings;

/**
 * @author Gwindow
 * @since Jun 3, 2012 9:43:25 AM
 */
public class DownloadDialog extends AlertDialog.Builder implements OnClickListener {
	private static final String DIALOG_TITLE = "Download: ";
	private static final String DOWNLOAD_BUTTON = "Download Torrent";
	private static final String PYWA_BUTTON = "Send to pyWA";
	private final int torrentId;
	private final String downloadUrl;
	private final Number size;
	private final Number snatches;
	private final Number seeders;
	private final Number leechers;
    private final String name;
	private final Context context;

	/**
	 * @todo descriptions
	 */
	public DownloadDialog(Context context, Number torrentId, String downloadUrl,
			Number size, Number snatches, Number seeders, Number leechers, String name) {
		super(context);
		this.context = context;
		this.downloadUrl = downloadUrl;
		this.torrentId = torrentId.intValue();

		this.leechers = leechers;
		this.seeders = seeders;
		this.snatches = snatches;
		this.size = size;
        this.name = name;

		init();
	}

	private void init() {
		setTitle(DIALOG_TITLE + name);
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
            new SendPyWa().execute(host, port, password, Integer.toString(torrentId), name);
		} else {
			Toast.makeText(context, "Fill out pyWA information in Settings",
					Toast.LENGTH_LONG).show();
		}
	}


    /**
     * Async task to send a torrent to the pyWa server desired
     * Should some kind of timeout be setup? When I tried to send without pyWa running
     * I didn't hear back from the task for ~3min
     * Also I'd like to show artist/torrentname in the toast we make in case it took a while
     * or multiple torrents have been trying to send. This would mean adding more
     * params sent to the download dialog
     */
    private class SendPyWa extends AsyncTask<String, Void, Boolean> {
        private String name;
        /**
         * Have the task send a torrent to the pyWa server
         * @param params 4 strings, 0: hostname, 1: port, 2: pyWa password
         *               3: torrent id, 4: torrent name
         * @return true if sent successfully, false if failed
         */
        @Override
        protected Boolean doInBackground(String... params){
            name = params[4];
            String url = params[0] + ":" + params[1] + "/dl.pywa?pass=" + params[2]
                    + "&site=whatcd&id=" + params[3];
            System.out.println("Sending torrent to: " + url);
            try {
                MySoup.scrapeOther(url);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if (status){
                System.out.println("Torrent passed!");
                Toast.makeText(context, "Sent: " + name, Toast.LENGTH_SHORT).show();
            }
            else {
                System.out.println("Failed sending");
                Toast.makeText(context, "Failed sending: " + name, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
