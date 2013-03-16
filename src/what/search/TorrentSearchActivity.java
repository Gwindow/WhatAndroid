package what.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import api.search.torrents.TorrentGroup;
import api.search.torrents.TorrentSearch;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import what.gui.*;
import what.torrents.artist.ArtistActivity;
import what.torrents.torrents.TorrentGroupActivity;

import java.util.List;

/**
 * @author Gwindow
 * @since May 28, 2012 11:04:47 AM
 */
public class TorrentSearchActivity extends MyActivity2 implements Scrollable, OnClickListener, OnEditorActionListener {
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
	private String searchString;

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
			searchString = bundle.getString(BundleKeys.SEARCH_STRING);
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
		searchTermField.setOnEditorActionListener(this);
		tagField = (EditText) this.findViewById(R.id.tagsearchBar);
		tagField.setOnEditorActionListener(this);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		setActionBarTitle("Torrent Search");
		if (searchString != null) {
			searchTermField.setText(searchString);
			search(null);
		}
	}

	/**
	 * Populate with search results
	 */
	public void populate() {
		setActionBarTitle("Torrent Search, " + torrentSearchPage + "/" + torrentSearch.getResponse().getPages());
		List<TorrentGroup> results = torrentSearch.getResponse().getResults();

		if (results != null) {
            for (TorrentGroup torrentGroup : results){
				LinearLayout result_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.torrent_search_result, null);

				// not all results have artists
				if (torrentGroup.getArtist() != null) {
					TextView artist = (TextView) result_layout.findViewById(R.id.artistTitle);
					artist.setText(torrentGroup.getArtist() + " - ");
					if (torrentGroup.getTorrents().get(0).getArtists() != null
                        && !torrentGroup.getTorrents().get(0).getArtists().isEmpty())
                    {
						artist.setId(torrentGroup.getTorrents().get(0).getArtists().get(0).getAliasid().intValue());
					}
					artist.setTag(ARTIST_TAG);
					artist.setOnClickListener(this);
				}
				TextView group = (TextView) result_layout.findViewById(R.id.groupTitle);
				group.setText(torrentGroup.getGroupName());
				group.setTag(TORRENT_GROUP_TAG);
				group.setId(torrentGroup.getGroupId().intValue());
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
			searchTerm = searchTermField.getText().toString();
			// if (searchTerm.length() > 0) {
			tagSearchTerm = tagField.getText().toString();
			// resets variables for the search
			torrentSearchPage = 1;
			setActionBarTitle("Torrent Search");
			toggleSearchBars();

			if (scrollLayout.getChildCount() > 0) {
				scrollLayout.removeAllViews();
			}

			new Load().execute();
			// }
		} else {
			toggleSearchBars();
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case ARTIST_TAG:
				if (v.getId() != View.NO_ID) {
					openArtist(v.getId());
				}
				break;
			case TORRENT_GROUP_TAG:
				openTorrentGroup(v.getId());
				break;
			default:
				break;
		}
	}

	private void openArtist(int id) {
		Intent intent = new Intent(this, ArtistActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.ARTIST_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
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
		if (item.getItemId() == android.R.id.home) {
			return homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	private void jumpToPage() {
		// TODO fix null pointer
		new JumpToPageDialog(this, torrentSearch.getResponse().getPages()) {
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
			areSearchBarsHidden = false;
		} else {
			searchTermField.setVisibility(View.GONE);
			tagField.setVisibility(View.GONE);
			areSearchBarsHidden = true;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId,
			KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			search(null);
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			return true;
		}
		return false;
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		private ProgressDialog dialog;
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			this(false);
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
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
            //Strip spaces from the tags, leaving them in results in an illegal character exception
            tagSearchTerm = tagSearchTerm.replaceAll("\\s", "");
			torrentSearch = TorrentSearch.torrentSearchFromSearchTermAndTags(searchTerm, tagSearchTerm, torrentSearchPage);
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
