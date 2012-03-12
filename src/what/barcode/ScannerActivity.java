package what.barcode;

import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import api.search.crossreference.CrossReference;
import api.search.requests.RequestsSearch;
import api.search.torrents.TorrentSearch;
import api.util.Triple;

public class ScannerActivity extends MyActivity implements OnClickListener, DialogInterface.OnClickListener {
	private static final String ZXING_MARKETPLACE_URL =
			"https://play.google.com/store/apps/details?id=com.google.zxing.client.android";
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
		searchType = SearchType.REQUESTSSEARCH;
		startScanner();
	}

	public void scanTorrents(View v) {
		searchType = SearchType.TORRENTSEARCH;
		startScanner();
	}

	public void quickScan(View v) {
		Intent i = new Intent(ScannerActivity.this, QuickScannerActivity.class);
		startActivity(i);
	}

	private void startScanner() {
		try {
			intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
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
				new LoadSearchResults().execute();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void displayEditTextPopup() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(""); alert.setMessage("Enter UPC code");
		final EditText input = new EditText(this); alert.setView(input);
		alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				upc = input.getText().toString();
				if (upc.length() > 0) {
					//new LoadSearchResults().execute();
					displayManualSearchTypePopup();
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

	public void displayManualSearchTypePopup() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(""); alert.setMessage("Select a Search Type");

		alert.setPositiveButton("Torrents", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				searchType = SearchType.TORRENTSEARCH;
				new LoadSearchResults().execute();
			}
		});

		alert.setNegativeButton("Requests", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				searchType = SearchType.REQUESTSSEARCH;
				new LoadSearchResults().execute();
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
		AlertDialog alert = new AlertDialog.Builder(ScannerActivity.this).create();

		alert.setTitle("Results Found");

		if ((torrents > 0) && (requests > 0)) {
			alert.setMessage("Found " + torrents + " torrents and " + requests + " requests");
			alert.setButton(AlertDialog.BUTTON1, "Torrents", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openTorrentSearch();
				}
			});
			alert.setButton(AlertDialog.BUTTON2, "Requests", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openRequestSearch();
				}
			});
			alert.setButton(AlertDialog.BUTTON3, "Buy", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					buy();
				}
			});
		}

		if ((torrents > 0) && (requests == 0)) {
			alert.setMessage("Found " + torrents + " torrents");
			alert.setButton(AlertDialog.BUTTON1, "Torrents", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openTorrentSearch();
				}
			});
			alert.setButton(AlertDialog.BUTTON2, "Buy", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					buy();
				}
			});
		}

		if ((torrents == 0) && (requests > 0)) {
			alert.setMessage("Found " + requests + " requests");
			alert.setButton(AlertDialog.BUTTON1, "Requests", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openRequestSearch();
				}
			});
			alert.setButton(AlertDialog.BUTTON2, "Buy", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					buy();
				}
			});
		}

		alert.setCancelable(true);
		alert.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int item) {

	}

	@Override
	public void onClick(View v) {

	}

	private class LoadSearchResults extends AsyncTask<Void, Void, Triple<Boolean, Integer, Integer>> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ScannerActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Triple<Boolean, Integer, Integer> doInBackground(Void... params) {
			boolean status = false;
			int torrentsFound = 0, requestsFound = 0;
			if (searchType == SearchType.TORRENTSEARCH) {
				try {
					torrentSearch = CrossReference.crossReferenceTorrentsByUPC(upc);
					if (torrentSearch.getResponse().getResults() != null) {
						torrentsFound = torrentSearch.getResponse().getResults().size();
						status = true;
					}
				} catch (Exception e) {
					status = false;
				}
			}
			if (searchType == SearchType.REQUESTSSEARCH) {
				try {
					requestsSearch = CrossReference.crossReferenceRequestsByUPC(upc);
					if (requestsSearch.getResponse().getResults() != null) {
						requestsFound = requestsSearch.getResponse().getResults().size();
						status = true;
					}
				} catch (Exception e) {
					status = false;
				}
			}
			return new Triple<Boolean, Integer, Integer>(status, torrentsFound, requestsFound);
		}

		@Override
		protected void onPostExecute(Triple<Boolean, Integer, Integer> status) {
			dialog.dismiss();
			searchTerm = CrossReference.getDeterminedSearchTerm();
			if (status.getA() == true) {
				if ((status.getB() > 0) || (status.getC() > 0)) {
					displayFoundPopup(status.getB(), status.getC());
					unlockScreenRotation();
				} else {
					displayNotFoundPopup();
					unlockScreenRotation();
				}
			}
			if (status.getA() == false) {
				Toast.makeText(ScannerActivity.this, "Scan Failed", Toast.LENGTH_SHORT).show();
				// displayNotFoundPopup();
				unlockScreenRotation();
			}
		}
	}

	enum SearchType {
		TORRENTSEARCH, REQUESTSSEARCH, TORRENTANDREQUESTSSEARCH;
	}
}
