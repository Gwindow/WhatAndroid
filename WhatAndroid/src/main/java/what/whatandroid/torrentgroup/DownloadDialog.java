package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;

/**
 * Alert dialog for selecting where/how to download torrents, ie to the device
 * or to some pyWA server
 */
public class DownloadDialog extends DialogFragment {
	private Torrents torrent;
	private DownloadDialogListener listener;

	/**
	 * Create a download dialog to prompt some user how they want to download
	 * the desired torrent
	 *
	 * @param t torrent to download
	 */
	public DownloadDialog(Torrents t){
		torrent = t;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle("Download Torrent")
			.setPositiveButton(R.string.send_to_pywa, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					if (listener != null){
						listener.sendToPywa(torrent);
					}
				}
			})
			.setNeutralButton(R.string.download_to_phone, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					if (listener != null){
						listener.downloadToPhone(torrent);
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					DownloadDialog.this.getDialog().cancel();
				}
			});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			listener = (DownloadDialogListener)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement DownloadDialogListener");
		}
	}

	/**
	 * Interface to get back information on the user's selection
	 */
	public interface DownloadDialogListener {
		/**
		 * If the user wants to download the torrent to their PyWA server
		 *
		 * @param t torrent to send
		 */
		public void sendToPywa(Torrents t);

		/**
		 * If the user wants to download the torrent to their phone
		 */
		public void downloadToPhone(Torrents t);
	}
}
