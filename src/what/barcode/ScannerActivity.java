package what.barcode;

import what.gui.MyTabActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import api.products.ProductSearch;

public class ScannerActivity extends MyTabActivity implements OnClickListener {
	private static final String KEY = "AIzaSyDOPEJep1GSxaWylXm7Tvdytozve8odmuo";
	private Intent intent;
	private String contents;
	private String format;
	private Button buyButton;
	private ProgressDialog dialog;
	private String upc, searchterm;
	private ProductSearch productSearch;
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Button torrentsButton, requestsButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.scanner, true);
		buyButton = (Button) this.findViewById(R.id.buybutton);
		torrentsButton = (Button) this.findViewById(R.id.torrentsbutton);
		requestsButton = (Button) this.findViewById(R.id.requestsbutton);

		setButtonState(torrentsButton, false);
		setButtonState(requestsButton, false);
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

	public void torrents(View v) {
		openTorrentSearch();
	}

	public void requests(View v) {
		openRequestSearch();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				upc = contents;
				new LoadSearchResults().execute();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scan failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void populateLayout() {

		Toast.makeText(this, "name: " + searchterm, Toast.LENGTH_LONG).show();
		// if music doesnt exsist on what open up a popup
		// displayMessagePopup();
		// else {
		// populate layout with search results
		// }
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

	public void displayNotFoundPopup() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("Could not find this music on What.CD");

		alert.setPositiveButton("Buy it", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				buy(null);
			}
		});

		alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
	}

	private void openTorrentSearch() {
		Bundle b = new Bundle();
		intent = new Intent(this, what.search.TorrentSearchActivity.class);
		b.putString("searchTerm", searchterm);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void openRequestSearch() {
		Bundle b = new Bundle();
		intent = new Intent(this, what.search.RequestSearchActivity.class);
		b.putString("searchTerm", searchterm);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void displayFoundPopup(int torrents, int requests) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");

		if ((torrents > 0) && (requests > 0)) {
			alert.setMessage("Found " + torrents + " and " + requests);
			final CharSequence[] items = { "Torrents", "Requests", "Buy it", "Close" };

			alert.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						openTorrentSearch();
					}
					if (item == 1) {
						openRequestSearch();
					}
					if (item == 2) {
						buy(null);
					}

				}
			});
		}

		if ((torrents > 0) && (requests == 0)) {
			alert.setMessage("Found " + torrents + " and " + requests);
			final CharSequence[] items = { "Torrents", "Buy it", "Close" };

			alert.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						openTorrentSearch();
					}
					if (item == 1) {
						buy(null);
					}

				}
			});
		}

		if ((torrents == 0) && (requests > 0)) {
			alert.setMessage("Found " + torrents + " and " + requests);
			final CharSequence[] items = { "Requests", "Buy it", "Close" };

			alert.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						openRequestSearch();
					}
					if (item == 1) {
						buy(null);
					}
				}
			});
		}

		alert.show();
	}

	@Override
	public void onClick(View v) {
		// for (int i = 0; i < (resultList.size()); i++) {
		// if (v.getId() == resultList.get(i).getId()) {
		// openUser(i);
		// }
		// }
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
			productSearch = ProductSearch.productSearchFromUPC(upc);
			if (productSearch.hasItems()) {
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
				Toast.makeText(ScannerActivity.this, "Could not find product", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
