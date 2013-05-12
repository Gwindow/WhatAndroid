package what.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.search.crossreference.CrossReference;
import api.search.requests.RequestsSearch;
import api.search.torrents.TorrentSearch;
import api.util.Triple;
import com.actionbarsherlock.app.SherlockFragment;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.MyActivity2;
import what.gui.R;

/**
 * This ScannerFragment is used to scan barcodes and get what we think is the artist/album
 * name from the WhatAPI CrossReference search
 */
public class ScannerFragment extends SherlockFragment implements OnClickListener {
    private static final String ZXING_MARKETPLACE_URL =
            "https://play.google.com/store/apps/details?id=com.google.zxing.client.android";
    private Intent intent;
    private String contents;
    private ProgressDialog dialog;
    private String upc, searchTerm;
    private TorrentSearch torrentSearch;
    private RequestsSearch requestsSearch;
    private SearchType searchType;
    private Button requestsButton, torrentsButton, manualButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scanner, container, false);
        requestsButton = (Button) view.findViewById(R.id.scanbutton_requests);
        requestsButton.setOnClickListener(this);
        torrentsButton = (Button) view.findViewById(R.id.scanbutton_torrents);
        torrentsButton.setOnClickListener(this);
        manualButton = (Button) view.findViewById(R.id.manualbutton);
        manualButton.setOnClickListener(this);

        return view;
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
            Toast.makeText(getSherlockActivity(), "Please scan or enter a upc code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                upc = contents;
                new LoadSearchResults().execute();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getSherlockActivity(), "Scan canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayEditTextPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
        alert.setTitle("");
        alert.setMessage("Enter UPC code");
        final EditText input = new EditText(getSherlockActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                upc = input.getText().toString();
                if (upc.length() > 0) {
                    // new LoadSearchResults().execute();
                    displayManualSearchTypePopup();
                } else {
                    Toast.makeText(ScannerFragment.this.getSherlockActivity(), "UPC not entered", Toast.LENGTH_LONG).show();
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

    private void displayManualSearchTypePopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
        alert.setTitle("");
        alert.setMessage("Select a Search Type");

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

    private void displayNotFoundPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());

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
        intent = new Intent(getSherlockActivity(), what.search.TorrentSearchActivity.class);
        b.putString(BundleKeys.SEARCH_STRING, searchTerm);
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    private void openRequestSearch() {
        Bundle b = new Bundle();
        intent = new Intent(getSherlockActivity(), what.search.RequestsSearchActivity.class);
        b.putString(BundleKeys.SEARCH_STRING, searchTerm);
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    private void displayFoundPopup(int torrents, int requests) {
        AlertDialog alert = new AlertDialog.Builder(getSherlockActivity()).create();

        alert.setTitle("Results Found");

        if ((torrents > 0) && (requests > 0)) {
            alert.setMessage("Found " + torrents + " torrents and " + requests + " requests");
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Torrents", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openTorrentSearch();
                }
            });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Requests", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openRequestSearch();
                }
            });
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Buy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buy();
                }
            });
        }

        if ((torrents > 0) && (requests == 0)) {
            alert.setMessage("Found " + torrents + " torrents");
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Torrents", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openTorrentSearch();
                }
            });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Buy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buy();
                }
            });
        }

        if ((torrents == 0) && (requests > 0)) {
            alert.setMessage("Found " + requests + " requests");
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Requests", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openRequestSearch();
                }
            });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Buy", new DialogInterface.OnClickListener() {
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
    public void onClick(View v) {
        if (v.getId() == torrentsButton.getId()) {
            searchType = SearchType.TORRENTSEARCH;
            startScanner();
        }
        if (v.getId() == requestsButton.getId()) {
            searchType = SearchType.REQUESTSSEARCH;
            startScanner();
        }
        if (v.getId() == manualButton.getId()) {
            displayEditTextPopup();
        }
    }

    private class LoadSearchResults extends AsyncTask<Void, Void, Triple<Boolean, Integer, Integer>> implements Cancelable {
        public LoadSearchResults() {
            ((MyActivity2) getSherlockActivity()).attachCancelable(this);
        }

        @Override
        public void cancel() {
            Log.d("cancel", "cancelled avatar");
            super.cancel(true);
        }

        @Override
        protected void onPreExecute() {
            lockScreenRotation();
            dialog = new ProgressDialog(getSherlockActivity());
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
            if (status.getA()) {
                if ((status.getB() > 0) || (status.getC() > 0)) {
                    displayFoundPopup(status.getB(), status.getC());
                    unlockScreenRotation();
                } else {
                    displayNotFoundPopup();
                    unlockScreenRotation();
                }
            } else {
                Toast.makeText(getSherlockActivity(), "CD not recognized or scan failed", Toast.LENGTH_SHORT).show();
                // displayNotFoundPopup();
                unlockScreenRotation();
            }
        }
    }

    public void lockScreenRotation() {
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (getSherlockActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (getSherlockActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (getSherlockActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (getSherlockActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;

        }
    }

    public void unlockScreenRotation() {
        getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    enum SearchType {
        TORRENTSEARCH, REQUESTSSEARCH, TORRENTANDREQUESTSSEARCH;
    }
}
