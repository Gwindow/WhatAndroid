package what.search;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.search.user.Results;
import api.search.user.UserSearch;

public class UserSearchActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> resultList = new ArrayList<TextView>();
	private LinearLayout scrollLayout;
	private UserSearch userSearch;
	private Intent intent;
	private ProgressDialog dialog;
	private String searchTerm;
	private EditText searchBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usersearch);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		searchBar = (EditText) this.findViewById(R.id.searchBar);
	}

	public void search(View v) {
		searchTerm = searchBar.getText().toString().trim();
		if (searchTerm.length() > 0) {
			clear();
			new LoadSearchResults().execute();
		} else {
			Toast.makeText(this, "Nothing to search for", Toast.LENGTH_SHORT).show();
		}
	}

	private void populateLayout() {
		List<Results> results = userSearch.getResponse().getResults();

		if (!results.isEmpty()) {

			for (int i = 0; i < results.size(); i++) {
				if ((i % 2) == 0) {
					resultList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
				} else {
					resultList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
				}
				resultList.get(i).setText(results.get(i).getUsername());
				resultList.get(i).setTextSize(18);
				resultList.get(i).setId(i);
				resultList.get(i).setOnClickListener(this);
				scrollLayout.addView(resultList.get(i));
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

	private void openUser(int i) {
		Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (resultList.size()); i++) {
			if (v.getId() == resultList.get(i).getId()) {
				openUser(i);
			}
		}
	}

	private class LoadSearchResults extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(UserSearchActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			userSearch = UserSearch.userSearchFromSearchTerm(searchTerm);
			return userSearch.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			if (status == false) {
				Toast.makeText(UserSearchActivity.this, "Could not load search results", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
