package what.barcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import what.gui.R;
import what.settings.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class QuickScannerFragment extends SherlockFragment {
	private static final String ZXING_MARKETPLACE_URL =
			"https://play.google.com/store/apps/details?id=com.google.zxing.client.android";
	private static final String FILENAME = "barcodes.txt";
	private static String extStorageDirectory = null;
	private Intent intent;
	private String upc;
	private FileOutputStream fileOutputStream;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.quickscanner, container, false);
		if (Settings.getQuickScannerFirstRun()) {
			showInstructions();
			Settings.saveQuickScannerFirstRun(false);
		}
		return view;
	}

	private void writeToFile(String s) throws IOException {
		File file = new File(extStorageDirectory, FILENAME);
		fileOutputStream = new FileOutputStream(file, true);
		String barcode = s + ",";
		fileOutputStream.write(barcode.getBytes());
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	private void showInstructions() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
		alert.setTitle("Instructions");
		alert.setMessage("Scan as many barcodes as your heart desires. These barcodes will be saved to a file which can be analyzed by different program on your computer to determine what the site is missing.");
		alert.setPositiveButton("Close", null);
		alert.setCancelable(true);
		alert.create().show();

	}

	public void scan(View v) {
		startScanner();
	}

	public void clear(View v) {
		File file = new File(extStorageDirectory, FILENAME);
		file.delete();
		Toast.makeText(getSherlockActivity(), "Barcodes deleted", Toast.LENGTH_SHORT).show();
	}

	public void send(View v) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		File file = new File(extStorageDirectory, FILENAME);
		Uri uri = Uri.fromFile(file);
		intent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Send Barcodes.."));
	}

	private void startScanner() {
		try {
			intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
			alert.setTitle("Barcode Scanner not found");
			alert.setMessage("The Zxing Barcode Scanner is required to use the scanning features, would you like to install it?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openMarketPlaceUrl(ZXING_MARKETPLACE_URL);
				}
			});
			alert.setNegativeButton("No", null);
			alert.setCancelable(true);
			alert.create().show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				upc = intent.getStringExtra("SCAN_RESULT");
				try {
					writeToFile(upc);
					Toast.makeText(getSherlockActivity(), "Barcode saved", Toast.LENGTH_SHORT).show();

				} catch (IOException e) {
					Toast.makeText(getSherlockActivity(), "Could not write barcode to file", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getSherlockActivity(), "Scan failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void openMarketPlaceUrl(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
}
