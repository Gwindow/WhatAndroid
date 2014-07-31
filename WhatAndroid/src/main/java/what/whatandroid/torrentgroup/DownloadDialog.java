package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import api.torrents.torrents.Torrents;
import what.whatandroid.R;

/**
 * Alert dialog for selecting where/how to download torrents, ie to the device
 * or to some pyWA server
 */
public class DownloadDialog extends DialogFragment {
	private static final String TORRENT_ID = "what.whatandroid.DOWNLOAD_TORRENT_ID",
		DOWNLOAD_LINK = "what.whatandroid.DOWNLOAD_LINK", DOWNLOAD_TITLE = "what.whatandroid.DOWNLOAD_TITLE",
		EDITION = "what.whatandroid.EDITION";

	/**
	 * Interface to get back information on the user's selection
	 */
	public interface DownloadDialogListener {
		/**
		 * If the user wants to download the torrent to their PyWA server
		 *
		 * @param torrentId id of the torrent to download
		 */
		public void sendToPywa(int torrentId);

		/**
		 * If the user wants to download the torrent to their phone
		 *
		 * @param torrentId id of the torrent to download
		 * @param link  download link for the torrent
		 * @param title the title of the torrent being downloaded
		 */
		public void downloadToPhone(int torrentId, String link, String title);
	}

	private DownloadDialogListener listener;

	/**
	 * Use this factory method to create a download dialog to prompt some user how they want to download
	 * the desired torrent
	 *
	 * @param t torrent to download
	 */
	public static DownloadDialog newInstance(String title, Torrents t){
		DownloadDialog f = new DownloadDialog();
		Bundle args = new Bundle();
		args.putInt(TORRENT_ID, t.getId().intValue());
		args.putString(DOWNLOAD_LINK, t.getDownloadLink());
		args.putString(DOWNLOAD_TITLE, title);
		args.putString(EDITION, t.getShortTitle());
		f.setArguments(args);
		return f;
	}

	public DownloadDialog(){
		//Required empty ctor
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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Bundle args = getArguments();
		final int torrentId = args.getInt(TORRENT_ID);
		final String downloadLink = args.getString(DOWNLOAD_LINK);
		final String title = args.getString(DOWNLOAD_TITLE);
		final String edition = args.getString(EDITION);
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
			android.R.style.Theme_Holo_Dialog));

		builder.setTitle("Download " + title)
			.setMessage("Edition: " + edition)
			.setPositiveButton(R.string.send_to_pywa, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					if (listener != null){
						listener.sendToPywa(torrentId);
					}
				}
			})
			.setNegativeButton(R.string.download_to_phone, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					if (listener != null){
						listener.downloadToPhone(torrentId, downloadLink, title + " " + edition);
					}
				}
			});
		return builder.create();
	}
}
