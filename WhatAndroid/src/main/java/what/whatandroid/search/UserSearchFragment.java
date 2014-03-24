package what.whatandroid.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import api.search.user.UserSearch;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.profile.ProfileActivity;

/**
 * Fragment for searching for users. If only one user is returned as a result we go to their profile,
 * otherwise a list of found users is displayed
 */
public class UserSearchFragment extends Fragment implements View.OnClickListener, OnLoggedInCallback {
	/**
	 * Search terms sent to us through the intent
	 */
	private String searchTerms;
	/**
	 * The first page of the user search results
	 */
	private UserSearch userSearch;
	/**
	 * The search terms input box
	 */
	private EditText editTerms;
	/**
	 * The list of search results and the search result adapter
	 */
	private ListView resultsList;
	private UserSearchAdapter resultsAdapter;
	/**
	 * The loading status footer
	 */
	private View footer;

	/**
	 * Create a user search fragment and have it start lodaing the search desired then the view
	 * is created. If the terms are empty then no search will be launched
	 *
	 * @param terms terms to search users for
	 * @return the user search fragment
	 */
	public static UserSearchFragment newInstance(String terms){
		UserSearchFragment f = new UserSearchFragment();
		f.searchTerms = terms;
		return f;
	}

	public UserSearchFragment(){
		//Required empty ctor
	}


	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			SetTitleCallback callback = (SetTitleCallback)activity;
			callback.setTitle("User Search");
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallback");
		}
	}

	@Override
	public void onLoggedIn(){
		//If we were sent a search to load from the intent, start loading it
		if (searchTerms != null){
			new LoadUserSearch().execute(searchTerms);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		resultsList = (ListView)view.findViewById(R.id.list);

		View header = inflater.inflate(R.layout.header_search, null);
		editTerms = (EditText)header.findViewById(R.id.search_terms);
		//Hide the unneeded tags box
		header.findViewById(R.id.search_tags).setVisibility(View.GONE);
		Button search = (Button)header.findViewById(R.id.search_button);
		search.setOnClickListener(this);
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		resultsList.addHeaderView(header);
		resultsList.addFooterView(footer);

		resultsAdapter = new UserSearchAdapter(getActivity(), footer);
		if (userSearch != null){
			resultsAdapter.viewSearch(userSearch);
		}
		//If we're not loading a search hide the loading indicator
		if (searchTerms == null || searchTerms.isEmpty()){
			footer.setVisibility(View.GONE);
		}
		else {
			editTerms.setText(searchTerms);
		}
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		resultsList.setOnScrollListener(resultsAdapter);
		return view;
	}


	@Override
	public void onClick(View v){
		searchTerms = editTerms.getText().toString();
		if (!searchTerms.isEmpty()){
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			new LoadUserSearch().execute(searchTerms);
		}
		else {
			Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
			editTerms.requestFocus();
		}
	}

	private class LoadUserSearch extends AsyncTask<String, Void, UserSearch> {
		@Override
		protected void onPreExecute(){
			if (footer != null){
				footer.setVisibility(View.VISIBLE);
			}
			if (resultsAdapter != null){
				resultsAdapter.clearSearch();
			}
			userSearch = null;
		}

		@Override
		protected UserSearch doInBackground(String... params){
			try {
				UserSearch s = UserSearch.search(params[0]);
				if (s != null && s.getStatus()){
					return s;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(UserSearch search){
			if (search != null){
				userSearch = search;
				//If there's only one user in the search go view their profile
				if (userSearch.getResponse().getResults().size() == 1){
					if (resultsAdapter != null){
						resultsAdapter.clearSearch();
					}
					if (footer != null){
						footer.setVisibility(View.GONE);
					}
					Intent intent = new Intent(getActivity(), ProfileActivity.class);
					intent.putExtra(ProfileActivity.USER_ID, userSearch.getResponse().getResults().get(0).getUserId().intValue());
					startActivity(intent);
				}
				else if (resultsAdapter != null){
					resultsAdapter.viewSearch(userSearch);
					if (footer != null && !userSearch.hasNextPage()){
						footer.setVisibility(View.GONE);
					}
				}
			}
			else {
				Toast.makeText(getActivity(), "Failed to load search results", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
