package what.barcode;

import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.products.ProductSearch;

public class ScannerActivity extends MyActivity {
	private static final String KEY = "AIzaSyDOPEJep1GSxaWylXm7Tvdytozve8odmuo";
	private Intent intent;
	private String contents;
	private String format;
	private Button buyButton;
	private ProgressDialog dialog;
	private String upc, searchterm;
	private ProductSearch productSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.scanner);
		buyButton = (Button) this.findViewById(R.id.buybutton);
	}

	public void scan(View v) {
		intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.setPackage("com.google.zxing.client.android");
		intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
		startActivityForResult(intent, 0);
	}

	public void buy(View v) {
		if (upc.length() > 0) {
			String url = "http://www.google.com/m/products?q=" + upc + "&source=zxing";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		} else {
			Toast.makeText(this, "Please scan or enter a upc code", Toast.LENGTH_SHORT).show();
		}
	}

	public void manual(View v) {
		displayEditTextPopup();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				upc = contents;
				test();
				new LoadSearchResults().execute();
			} else if (resultCode == RESULT_CANCELED) {
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
		Toast.makeText(this, "name: " + searchterm, Toast.LENGTH_LONG).show();
	}

	public void displayEditTextPopup() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("Enter UPC code");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				upc = input.getText().toString();
				new LoadSearchResults().execute();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
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
			productSearch = ProductSearch.ProductSearchFromUPC(upc);
			if (productSearch.getStatus()) {
				searchterm = productSearch.getItems().get(0).getProduct().getTitle();
				// TODO do what search
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			if (status == false) {
				Toast.makeText(ScannerActivity.this, "Could not load ", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
