package what.search;

import java.util.List;

import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.torrents.torrents.TorrentGroupActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.search.torrents.Results;
import api.search.torrents.TorrentSearch;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since May 28, 2012 11:04:47 AM
 */
public class TorrentSearchActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int ARTIST_TAG = 0;
	private static final int TORRENT_GROUP_TAG = 1;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private EditText searchTermField;
	private EditText tagField;

	private TorrentSearch torrentSearch;
	private int torrentSearchPage = 1;

	private String searchTerm = "";
	private String tagSearchTerm = "";

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

		List<Results> results = torrentSearch.getResponse().getResults();

		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				LinearLayout result_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.torrent_search_result, null);

				// not all results have artists
				if (results.get(i).getArtist() != null) {
					TextView artist = (TextView) result_layout.findViewById(R.id.artistTitle);
					artist.setText(results.get(i).getArtist() + " - ");
					// TODO add artist id
					artist.setTag(ARTIST_TAG);
					artist.setOnClickListener(this);
				}

				TextView group = (TextView) result_layout.findViewById(R.id.groupTitle);
				group.setText(results.get(i).getGroupName());
				group.setTag(TORRENT_GROUP_TAG);
				group.setId(results.get(i).getGroupId().intValue());
				group.setOnClickListener(this);

				scrollLayout.addView(result_layout);
			}
			if (results.isEmpty()) {
				LinearLayout result_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.torrent_search_result, null);
				TextView group = (TextView) result_layout.findViewById(R.id.groupTitle);
				group.setText("Nothing found");
				scrollLayout.addView(result_layout);
			}
		}
	}

	/**
	 * Called when search button is clicked, starts the search.
	 * 
	 * @param v
	 */
	public void search(View v) {
		if (!areSearchBarsHidden) {
			searchTerm = searchTermField.getText().toString().trim();
			if (searchTerm.length() > 0) {
				tagSearchTerm = tagField.getText().toString().trim();
				// resets variables for the search
				torrentSearchPage = 1;
				setActionBarTitle("Torrent Search");
				toggleSearchBars();
				new Load().execute();
			}
		} else {
			toggleSearchBars();
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case ARTIST_TAG:
				// TODO finish
				break;
			case TORRENT_GROUP_TAG:
				openTorrentGroup(v.getId());
				break;
			default:
				break;
		}
	}

	private void openTorrentGroup(int id) {
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.TORRENT_GROUP_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.torrent_search_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.refresh_item:
				refresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void jumpToPage() {
		new JumpToPageDialog(this, torrentSearch.getResponse().getPages().intValue()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(TorrentSearchActivity.this, TorrentSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.TORRENT_SEARCH_PAGE, getPage());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}.create().show();
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
