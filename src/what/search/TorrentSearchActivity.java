package what.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import what.torrents.torrents.TorrentTabActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.search.torrents.TorrentSearch;

public class TorrentSearchActivity extends MyActivity implements OnClickListener {
	private ScrollView scrollView;
	private ArrayList<TextView> resultList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private TorrentSearch torrentSearch;
	private Intent intent;
	private ProgressDialog dialog;
	private String searchTerm = "", tagSearchTerm = "";
	private EditText searchBar, tagSearchBar;
	private Button backButton, nextButton;
	private int page;
	private HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
	private boolean searchBarsVisible = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrentsearch, true);
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);
		scrollView = (ScrollView) this.findViewById(R.id.scrollView);

		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		searchBar = (EditText) this.findViewById(R.id.searchBar);
		tagSearchBar = (EditText) this.findViewById(R.id.tagsearchBar);
		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		getBundle();

		// if the page is greater than one than get the search term and automatically search for it
		if ((page > 1) || (searchTerm.length() > 0)) {
			searchBar.setText(searchTerm);
			search(null);
		}
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		try {
			page = b.getInt("page");
		} catch (Exception e) {
			page = 1;
		}

		try {
			searchTerm = b.getString("searchTerm");
		} catch (Exception e) {
			searchTerm = "";
		}

		try {
			tagSearchTerm = b.getString("tagSearchTerm");
		} catch (Exception e) {
			tagSearchTerm = "";
		}
	}

	private void hideSearchBars() {
		searchBar.setVisibility(EditText.GONE);
		tagSearchBar.setVisibility(EditText.GONE);
		searchBarsVisible = false;
	}

	private void showSearchBars() {
		searchBar.setVisibility(EditText.VISIBLE);
		tagSearchBar.setVisibility(EditText.VISIBLE);
		searchBarsVisible = true;

	}

	public void search(View v) {
		if (searchBarsVisible) {
			searchTerm = searchBar.getText().toString().trim();
			tagSearchTerm = tagSearchBar.getText().toString().trim();
			if (searchTerm.length() < 0) {
				searchTerm = "";
			}
			if (tagSearchTerm.length() < 0) {
				tagSearchTerm = "";
			}
			// reset the page to 1 for the next search
			page = 1;
			clear();
			new LoadSearchResults().execute();
			hideSearchBars();
		} else {
			showSearchBars();
		}

	}

	private void populateLayout() {
		setButtonState(backButton, torrentSearch.hasPreviousPage());
		setButtonState(nextButton, torrentSearch.hasNextPage());

		List<api.search.torrents.Results> results = torrentSearch.getResponse().getResults();

		if (!results.isEmpty()) {

			for (int i = 0; i < results.size(); i++) {
				if ((i % 2) == 0) {
					resultList.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_even, null));
				} else {
					resultList.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_odd, null));
				}
				if (results.get(i).getGroupYear() != null) {
					resultList.get(i).setText(
							results.get(i).getGroupName() + " [" + results.get(i).getGroupYear().toString() + "]");
				} else {
					resultList.get(i).setText(results.get(i).getGroupName());
				}
				resultList.get(i).setTextSize(18);
				resultList.get(i).setId(i);
				resultList.get(i).setOnClickListener(this);
				scrollLayout.addView(resultList.get(i));
				idMap.put(i, results.get(i).getGroupId().intValue());
			}
		} else {
			Toast.makeText(this, "Nothing found", Toast.LENGTH_SHORT).show();
		}
	}

	private void clear() {
		try {
			resultList.clear();
			scrollLayout.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openTorrent(int id) {
		Bundle b = new Bundle();
		Intent intent = new Intent(TorrentSearchActivity.this, TorrentTabActivity.class);
		b.putInt("torrentGroupId", id);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (resultList.size()); i++) {
			if (v.getId() == resultList.get(i).getId()) {
				openTorrent(idMap.get(v.getId()));
			}
		}
	}

	public void next(View v) {
		if (torrentSearch.hasNextPage()) {
			Bundle b = new Bundle();
			intent = new Intent(TorrentSearchActivity.this, what.search.TorrentSearchActivity.class);
			b.putInt("page", page + 1);
			b.putString("searchTerm", searchTerm);
			b.putString("tagSearchTerm", tagSearchTerm);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void onRightGesturePerformed() {
		next(null);
	}

	@Override
	public void onDownGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);

	}

	private class LoadSearchResults extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(TorrentSearchActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			torrentSearch = TorrentSearch.torrentSearchFromSearchTermAndTags(searchTerm, tagSearchTerm, page);
			return torrentSearch.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(TorrentSearchActivity.this, "Could not load search results", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
