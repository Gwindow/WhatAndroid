package what.whatandroid.barcode;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

/**
 * Dialog to prompt user about which scanning mode they want to use, single or bulk
 * and show instructions for how to use each mode. When the user picks a mode
 * the corresponding callback is called to alert the containing activity to
 * start the appropriate intent
 */
public class ScannerDialog extends DialogFragment {
	private ScannerDialogListener listener;

	public ScannerDialog(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			listener = (ScannerDialogListener)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ScannerDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Select Scan Mode")
			.setMessage("Bulk scan: scan as many barcodes as you like then share your history to the app\n\nSingle scan: scan a single barcode")
			.setPositiveButton("Single Scan", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					listener.startSingleScan();
				}
			})
			.setNegativeButton("Bulk Scan", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					listener.startBulkScan();
				}
			});
		return builder.create();
	}

	public interface ScannerDialogListener {
		/**
		 * If the user wants to scan a single barcode this is called
		 */
		public void startSingleScan();

		/**
		 * If the uer wants to scan multiple barcodes this is called
		 */
		public void startBulkScan();
	}
}
