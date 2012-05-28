package what.search;

import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import api.search.torrents.TorrentSearch;

/**
 * @author Gwindow
 * @since May 28, 2012 11:04:47 AM
 */
public class TorrentSearchActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int ARTIST_TAG = 0;
	private static final int TORRENT_TAG = 1;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private EditText searchTermField;
	private EditText tagField;

	private TorrentSearch torrentSearch;
	private int torrentSearchPage = 1;

	private String searchTerm;
	private String tagSearchTerm;

	private boolean isLoaded;
	private boolean areSearchBarsHidden;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.SEARCH);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrent_search, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		try {
			torrentSearchPage = bundle.getInt(BundleKeys.TORRENT_SEARCH_PAGE);
		} catch (Exception e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		scrollView = (MyScrollView) this.findViewById(R.id.scrollView);
		scrollView.attachScrollable(this);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		searchTermField = (EditText) this.findViewById(R.id.searchBar);
		tagField = (EditText) this.findViewById(R.id.tagsearchBar);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		setActionBarTitle("Torrent Search");
	}

	/**
	 * Populate with search results
	 */
	public void populate() {
		setActionBarTitle("Torrent Search, " + torrentSearchPage + "/" + torrentSearch.getResponse().getPages());

	}

	/**
	 * Called when search button is clicked, starts the search.
	 * 
	 * @param v
	 */
	public void search(View v) {
		if (!areSearchBarsHidden) {
			if (searchTermField.length() > 0) {
				searchTerm = searchTermField.getText().toString().trim();
				tagSearchTerm = tagField.getText().toString().trim();
				// resets variables for the search
				torrentSearchPage = 1;
				setActionBarTitle("Torrent Search");
				toggleSearchBars();
				new Load().execute();
			} else {
				Toast.makeText(this, "No search term entered", Toast.LENGTH_SHORT).show();
			}
		} else {
			toggleSearchBars();
		}
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrolledToBottom() {
		nextPage();
	}

	/**
	 * Load the next page while currentPage < totalPages.
	 */
	private void nextPage() {
		if (isLoaded) {
			if (torrentSearchPage < torrentSearch.getResponse().getPages().intValue()) {
				torrentSearchPage++;
				new Load(true).execute();
			}
		}
	}

	private void toggleSearchBars() {
		if (areSearchBarsHidden) {
			searchTermField.setVisibility(View.VISIBLE);
			tagField.setVisibility(View.VISIBLE);
		} else {
			searchTermField.setVisibility(View.GONE);
			tagField.setVisibility(View.GONE);
		}
	}

	private class Load extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			super();
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(TorrentSearchActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
				dialog = new ProgressDialog(TorrentSearchActivity.this);
				dialog.setIndeterminate(true);
				dialog.setMessage("Loading...");
				dialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			torrentSearch = TorrentSearch.torrentSearchFromSearchTermAndTags(searchTerm, tagSearchTerm);
			return torrentSearch.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			isLoaded = true;
			if (useEmbeddedDialog) {
				hideProgressBar();
			} else {
				dialog.dismiss();
				unlockScreenRotation();
			}

			if (status) {
				populate();
			} else {
				ErrorToast.show(TorrentSearchActivity.this, TorrentSearchActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
