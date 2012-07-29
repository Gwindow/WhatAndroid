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
import what.user.UserActivity;
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
import api.search.user.UserSearch;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * The Class UserSearchActivity.
 * 
 * @author Gwindow
 * @since Jun 8, 2012 12:00:38 AM
 */
public class UserSearchActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int USER_TAG = 0;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private EditText searchTermField;

	private UserSearch userSearch;
	private int userSearchPage = 1;

	private String searchTerm = "";

	private boolean isLoaded;
	private boolean areSearchBarsHidden;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.SEARCH);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.user_search, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		try {
			userSearchPage = bundle.getInt(BundleKeys.USER_SEARCH_PAGE);
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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		setActionBarTitle("User Search");
	}

	/**
	 * Populate with search results
	 */
	public void populate() {
		setActionBarTitle("User Search, " + userSearchPage + "/" + userSearch.getResponse().getPages());

		List<api.search.user.Results> results = userSearch.getResponse().getResults();

		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				TextView username = (TextView) getLayoutInflater().inflate(R.layout.user_search_result, null);

				username.setText(results.get(i).getUsername());
				username.setTag(USER_TAG);
				username.setId(results.get(i).getUserId().intValue());
				username.setOnClickListener(this);

				scrollLayout.addView(username);
			}
			if (results.isEmpty()) {
				TextView result_layout = (TextView) getLayoutInflater().inflate(R.layout.user_search_result, null);
				result_layout.setText("Nothing found");
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
				// resets variables for the search
				userSearchPage = 1;
				setActionBarTitle("User Search");
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
			case USER_TAG:
				openUser(v.getId());
				break;
			default:
				break;
		}
	}

	private void openUser(int id) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, id);
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
			if (userSearchPage < userSearch.getResponse().getPages().intValue()) {
				userSearchPage++;
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
		new JumpToPageDialog(this, userSearch.getResponse().getPages()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(UserSearchActivity.this, UserSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.USER_SEARCH_PAGE, getPage());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}.create().show();
	}

	private void toggleSearchBars() {
		if (areSearchBarsHidden) {
			searchTermField.setVisibility(View.VISIBLE);
			areSearchBarsHidden = false;
		} else {
			searchTermField.setVisibility(View.GONE);
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
				bar = new ProgressBar(UserSearchActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
				dialog = new ProgressDialog(UserSearchActivity.this);
				dialog.setIndeterminate(true);
				dialog.setMessage("Loading...");
				dialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			userSearch = UserSearch.userSearchFromSearchTermAndPage(searchTerm, userSearchPage);
			return userSearch.getStatus();
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
				ErrorToast.show(UserSearchActivity.this, UserSearchActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
