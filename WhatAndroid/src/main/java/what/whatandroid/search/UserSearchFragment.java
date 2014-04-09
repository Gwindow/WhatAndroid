package what.whatandroid.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import api.search.user.UserSearch;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.callbacks.ViewUserCallbacks;

/**
 * Fragment for searching for users. If only one user is returned as a result we go to their profile,
 * otherwise a list of found users is displayed
 */
public class UserSearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener,
	OnLoggedInCallback, LoaderManager.LoaderCallbacks<UserSearch>, AbsListView.OnScrollListener {
	/**
	 * So we can set the action bar title and viewing a user
	 */
	private SetTitleCallback setTitle;
	private ViewUserCallbacks viewUser;
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
	private UserSearchAdapter resultsAdapter;
	/**
	 * The loading status footer
	 */
	private View footer;
	private TextView noResults;

	/**
	 * Create a user search fragment and have it start lodaing the search desired then the view
	 * is created. If the terms are empty then no search will be launched
	 *
	 * @param terms terms to search users for
	 * @return the user search fragment
	 */
	public static UserSearchFragment newInstance(String terms){
		UserSearchFragment f = new UserSearchFragment();
		Bundle args = new Bundle();
		args.putString(SearchActivity.TERMS, terms);
		f.setArguments(args);
		return f;
	}

	public UserSearchFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
			viewUser = (ViewUserCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitle and ViewUser callbacks");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			searchTerms = getArguments().getString(SearchActivity.TERMS, "");
		}
		else {
			searchTerms = savedInstanceState.getString(SearchActivity.TERMS, "");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		setTitle.setTitle("User Search");
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView resultsList = (ListView)view.findViewById(R.id.list);
		noResults = (TextView)view.findViewById(R.id.no_content_notice);
		noResults.setText("No Results");

		View header = inflater.inflate(R.layout.header_search, null);
		editTerms = (EditText)header.findViewById(R.id.search_terms);
		editTerms.setOnEditorActionListener(this);
		//Hide the unneeded tags box
		header.findViewById(R.id.search_tags).setVisibility(View.GONE);
		Button search = (Button)header.findViewById(R.id.search_button);
		search.setOnClickListener(this);
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		resultsList.addHeaderView(header);
		resultsList.addFooterView(footer);
		footer.setVisibility(View.GONE);

		resultsAdapter = new UserSearchAdapter(getActivity(), footer);
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		resultsList.setOnScrollListener(this);

		if (searchTerms.isEmpty()){
			editTerms.requestFocus();
		}
		else {
			editTerms.setText(searchTerms);
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(SearchActivity.TERMS, editTerms.getText().toString());
	}

	@Override
	public void onLoggedIn(){
		//Don't auto-search since this fragment redirects to user's profile if we only
		//get one result
	}

	@Override
	public void onClick(View v){
		searchTerms = editTerms.getText().toString();
		if (!searchTerms.isEmpty()){
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			resultsAdapter.clear();
			resultsAdapter.notifyDataSetChanged();
			footer.setVisibility(View.VISIBLE);
			startSearch(searchTerms, 1);
		}
		else {
			Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
			editTerms.requestFocus();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
		if (event == null || event.getAction() == KeyEvent.ACTION_DOWN){
			searchTerms = editTerms.getText().toString();
			if (!searchTerms.isEmpty()){
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
				resultsAdapter.clear();
				resultsAdapter.notifyDataSetChanged();
				footer.setVisibility(View.VISIBLE);
				startSearch(searchTerms, 1);
			}
			else {
				Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
				editTerms.requestFocus();
			}
		}
		return true;
	}

	@Override
	public Loader<UserSearch> onCreateLoader(int id, Bundle args){
		return new UserSearchAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<UserSearch> loader, UserSearch data){
		userSearch = data;
		if (userSearch != null){
			if (isAdded()){
				//If there's only one user in the search go view their profile
				if (userSearch.getResponse().getResults().size() == 1){
					resultsAdapter.clear();
					resultsAdapter.notifyDataSetChanged();
					footer.setVisibility(View.GONE);
					viewUser.viewUser(userSearch.getResponse().getResults().get(0).getUserId().intValue());
				}
				else {
					if (userSearch.getResponse().getResults().isEmpty()){
						noResults.setVisibility(View.VISIBLE);
					}
					if (userSearch.getPage() == 1){
						resultsAdapter.clear();
					}
					resultsAdapter.addAll(userSearch.getResponse().getResults());
					resultsAdapter.notifyDataSetChanged();
					if (!userSearch.hasNextPage()){
						footer.setVisibility(View.GONE);
					}
				}
			}
		}
		else if (isAdded()){
			Toast.makeText(getActivity(), "Could not load search results", Toast.LENGTH_LONG).show();
			footer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<UserSearch> loader){
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		if (userSearch != null && userSearch.hasNextPage() && firstVisibleItem + visibleItemCount + 10 >= totalItemCount){
			startSearch(searchTerms, userSearch.getPage() + 1);
		}
	}

	private void startSearch(String terms, int page){
		noResults.setVisibility(View.GONE);
		Bundle args = new Bundle();
		args.putString(SearchActivity.TERMS, terms);
		args.putInt(SearchActivity.PAGE, page);
		LoaderManager lm = getLoaderManager();
		Loader l = lm.getLoader(page);
		if (l == null){
			lm.initLoader(page, args, this);
		}
		//Check if the terms and tags are different from what the loader has and restart if they're different
		else {
			UserSearchAsyncLoader s = (UserSearchAsyncLoader)l;
			if (!s.getTerms().equalsIgnoreCase(searchTerms)){
				lm.restartLoader(page, args, this);
			}
			else {
				lm.initLoader(page, args, this);
			}
		}
	}
}
