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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.search.crossreference.CrossReference;
import api.search.requests.RequestsSearch;
import api.search.torrents.TorrentSearch;
import api.util.Triple;

public class ScannerActivity extends MyActivity implements OnClickListener, DialogInterface.OnClickListener {
	private static final String ZXING_MARKETPLACE_URL = "market://details?id=com.google.zxing.client.android";
	private Intent intent;
	private String contents;
	private ProgressDialog dialog;
	private String upc, searchTerm;
	private TorrentSearch torrentSearch;
	private RequestsSearch requestsSearch;
	private Button torrentsButton, requestsButton;
	private SearchType searchType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.scanner, true);
		torrentsButton = (Button) this.findViewById(R.id.torrentsbutton);
		requestsButton = (Button) this.findViewById(R.id.requestsbutton);

		setButtonState(torrentsButton, false);
		setButtonState(requestsButton, false);
	}

	public void scanRequests(View v) {
		try {
			intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			searchType = SearchType.REQUESTSSEARCH;
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

	public void scanTorrents(View v) {
		try {
			intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			searchType = SearchType.TORRENTSEARCH;
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

	private void openMarketPlaceUrl(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private void buy() {
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
				upc = contents;
				new LoadSearchResults().execute(new SearchType[] { searchType });
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scan failed", Toast.LENGTH_LONG).show();
			}
		}
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
				if (upc.length() > 0) {
					new LoadSearchResults().execute();
				} else {
					Toast.makeText(ScannerActivity.this, "UPC not entered", Toast.LENGTH_LONG).show();
				}
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
				buy();
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
		intent = new Intent(ScannerActivity.this, what.search.TorrentSearchActivity.class);
		b.putString("searchTerm", searchTerm);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void openRequestSearch() {
		Bundle b = new Bundle();
		intent = new Intent(ScannerActivity.this, what.search.RequestsSearchActivity.class);
		b.putString("searchTerm", searchTerm);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void displayFoundPopup(int torrents, int requests) {
		AlertDialog alert = new AlertDialog.Builder(this).create();

		alert.setTitle("Results Found");

		if ((torrents > 0) && (requests > 0)) {
			alert.setMessage("Found " + torrents + " torrents and " + requests + " requests");
			alert.setButton(AlertDialog.BUTTON1, "Torrents", this);
			alert.setButton(AlertDialog.BUTTON2, "Requests", this);
			alert.setButton(AlertDialog.BUTTON3, "Buy it", this);
		}

		if ((torrents > 0) && (requests == 0)) {
			alert.setMessage("Found " + torrents + " torrents");
			alert.setButton(AlertDialog.BUTTON1, "Torrents", this);
			alert.setButton(AlertDialog.BUTTON3, "Buy it", this);
		}

		if ((torrents == 0) && (requests > 0)) {
			alert.setMessage("Found " + requests + " requests");
			alert.setButton(AlertDialog.BUTTON2, "Requests", this);
			alert.setButton(AlertDialog.BUTTON3, "Buy it", this);
		}

		alert.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int item) {
		if (item == AlertDialog.BUTTON1) {
			openTorrentSearch();
		}
		if (item == AlertDialog.BUTTON2) {
			openRequestSearch();
		}
		if (item == AlertDialog.BUTTON3) {
			buy();
		}

	}

	@Override
	public void onClick(View v) {

	}

	private class LoadSearchResults extends AsyncTask<SearchType, Void, Triple<Boolean, Integer, Integer>> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ScannerActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Triple<Boolean, Integer, Integer> doInBackground(SearchType... params) {
			boolean status = false;
			int torrentsFound = 0, requestsFound = 0;
			switch (params[0]) {
			case TORRENTSEARCH:
				torrentSearch = CrossReference.crossReferenceTorrentsByUPC(upc);
				if (torrentSearch.getResponse().getResults() != null && !torrentSearch.getResponse().getResults().isEmpty())
					torrentsFound = torrentSearch.getResponse().getResults().size();
				status = true;
				break;
			case REQUESTSSEARCH:
				requestsSearch = CrossReference.crossReferenceRequestsByUPC(upc);
				if (requestsSearch.getResponse().getResults() != null && !requestsSearch.getResponse().getResults().isEmpty())
					requestsFound = requestsSearch.getResponse().getResults().size();
				status = true;
				break;
			}
			return new Triple<Boolean, Integer, Integer>(status, torrentsFound, requestsFound);
		}

		@Override
		protected void onPostExecute(Triple<Boolean, Integer, Integer> status) {
			dialog.dismiss();
			searchTerm = CrossReference.getDeterminedSearchTerm();
			if (status.getA() == true) {
				displayFoundPopup(status.getB(), status.getC());
				unlockScreenRotation();
			}
			if (status.getA() == false) {
				displayNotFoundPopup();
				unlockScreenRotation();
			}
		}
	}

	enum SearchType {
		TORRENTSEARCH, REQUESTSSEARCH, TORRENTANDREQUESTSSEARCH;
	}
}
