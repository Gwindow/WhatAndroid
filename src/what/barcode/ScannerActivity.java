package what.barcode;

import java.net.URLEncoder;

import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScannerActivity extends MyActivity {
	private Intent intent;
	private String contents;
	private String format;
	private Button buyButton;
	private boolean hasScanned = false;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setMenuLevels(new int[] { R.menu.homemenu_1, R.menu.homemenu_2 });
		super.setContentView(R.layout.scanner);
		buyButton = (Button) this.findViewById(R.id.buybutton);
		if (hasScanned == false) {
			buyButton.setEnabled(hasScanned);
		}
	}

	public void scan(View v) {
		intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.setPackage("com.google.zxing.client.android");
		intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
		startActivityForResult(intent, 0);
	}

	public void buy(View v) {
		String url = translateToSearchString(format);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private String translateToSearchString(String input) {
		input = URLEncoder.encode(input);
		String searchUrl = "http://www.google.com/search?q=" + input + "&tbm=shop&hl=en&aq=f";
		return searchUrl;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				hasScanned = true;
				test();
				new LoadSearchResults().execute();
			} else if (resultCode == RESULT_CANCELED) {
				hasScanned = false;
				buyButton.setEnabled(hasScanned);
				Toast.makeText(this, "Scan failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	// TODO remove
	private void test() {
		Toast.makeText(this, "Contents: " + contents, Toast.LENGTH_LONG).show();
		Toast.makeText(this, "Format: " + format, Toast.LENGTH_LONG).show();
	}

	private void populateLayout() {
	}

	public void displayAlert(String title, String postive, String negative, String message, Context context) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton(postive, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).setNegativeButton(negative, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).show();
	}

	private class LoadSearchResults extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ScannerActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// subscriptions = Subscriptions.init();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			populateLayout();
			if (status == false) {
				Toast.makeText(ScannerActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
