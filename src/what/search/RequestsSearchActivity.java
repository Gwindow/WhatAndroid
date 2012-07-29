package what.search;

import java.util.List;

import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.requests.RequestActivity;
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
import api.search.requests.RequestsSearch;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class RequestsSearchActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int REQUEST_TAG = 0;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private EditText searchTermField;
	private EditText tagField;

	private RequestsSearch requestsSearch;
	private int requestsSearchPage = 1;

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
			requestsSearchPage = bundle.getInt(BundleKeys.REQUESTS_SEARCH_PAGE);
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
		tagField = (EditText) this.findViewById(R.id.tagsearchBar);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		setActionBarTitle("Requests Search");
		if (searchString != null) {
			searchTermField.setText(searchString);
			search(null);
		}
	}

	/**
	 * Populate with search results
	 */
	public void populate() {
		setActionBarTitle("Requests Search, " + requestsSearchPage + "/" + requestsSearch.getResponse().getPages());

		List<api.search.requests.Results> results = requestsSearch.getResponse().getResults();

		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				LinearLayout result_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.torrent_search_result, null);

				TextView group = (TextView) result_layout.findViewById(R.id.groupTitle);
				group.setText(results.get(i).getTitle());
				group.setTag(REQUEST_TAG);
				group.setId(results.get(i).getRequestId().intValue());
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
				requestsSearchPage = 1;
				setActionBarTitle("Requests Search");
				toggleSearchBars();

				if (scrollLayout.getChildCount() > 0) {
					scrollLayout.removeAllViews();
				}

				new Load().execute();
			}
		} else {
			toggleSearchBars();
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REQUEST_TAG:
				openRequest(v.getId());
				break;
			default:
				break;
		}
	}

	private void openRequest(int id) {
		Intent intent = new Intent(this, RequestActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.REQUEST_ID, id);
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
			if (requestsSearchPage < requestsSearch.getResponse().getPages().intValue()) {
				requestsSearchPage++;
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
		new JumpToPageDialog(this, requestsSearch.getResponse().getPages()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(RequestsSearchActivity.this, RequestsSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.REQUESTS_SEARCH_PAGE, getPage());
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
				bar = new ProgressBar(RequestsSearchActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
				dialog = new ProgressDialog(RequestsSearchActivity.this);
				dialog.setIndeterminate(true);
				dialog.setMessage("Loading...");
				dialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			requestsSearch = RequestsSearch.requestSearchFromSearchTermAndTags(searchTerm, tagSearchTerm, requestsSearchPage);
			return requestsSearch.getStatus();
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
				ErrorToast.show(RequestsSearchActivity.this, RequestsSearchActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
