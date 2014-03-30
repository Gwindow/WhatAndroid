package what.whatandroid.search;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import api.search.requests.RequestsSearch;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for searching for requests
 */
public class RequestSearchFragment extends Fragment
	implements View.OnClickListener, TextView.OnEditorActionListener, OnLoggedInCallback {
	/**
	 * Search terms and tags sent to use by the intent
	 */
	private String searchTerms, searchTags;
	/**
	 * The torrent search we're viewing
	 */
	private RequestsSearch requestSearch;
	/**
	 * The search input boxes
	 */
	private EditText editTerms, editTags;
	/**
	 * The list of search results and the search result adapter
	 */
	private ListView resultsList;
	private RequestSearchAdapter resultsAdapter;
	/**
	 * The loading status footer
	 */
	private View footer;

	public static RequestSearchFragment newInstance(String terms, String tags){
		RequestSearchFragment fragment = new RequestSearchFragment();
		fragment.searchTerms = terms;
		fragment.searchTags = tags;
		return fragment;
	}

	public RequestSearchFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			SetTitleCallback callback = (SetTitleCallback)activity;
			callback.setTitle("Request Search");
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallbacks");
		}
	}

	@Override
	public void onLoggedIn(){
		if (searchTerms != null && requestSearch == null){
			if (searchTags == null){
				searchTags = "";
			}
			new LoadRequestSearch().execute(searchTerms, searchTags);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		resultsList = (ListView)view.findViewById(R.id.list);

		View header = inflater.inflate(R.layout.header_search, null);
		editTerms = (EditText)header.findViewById(R.id.search_terms);
		editTags = (EditText)header.findViewById(R.id.search_tags);
		editTags.setOnEditorActionListener(this);
		Button search = (Button)header.findViewById(R.id.search_button);
		search.setOnClickListener(this);
		footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		resultsList.addHeaderView(header);
		resultsList.addFooterView(footer);
		//We should only show the footer if we're loading a search
		footer.setVisibility(View.GONE);

		resultsAdapter = new RequestSearchAdapter(getActivity(), footer);
		if (requestSearch != null){
			resultsAdapter.viewSearch(requestSearch);
		}
		if (searchTerms == null || searchTerms.isEmpty()){
			editTerms.requestFocus();
		}
		else {
			editTerms.setText(searchTerms);
			editTags.setText(searchTags);
			if (requestSearch == null){
				footer.setVisibility(View.VISIBLE);
			}
		}
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		resultsList.setOnScrollListener(resultsAdapter);
		return view;
	}

	@Override
	public void onClick(View v){
		searchTerms = editTerms.getText().toString();
		searchTags = editTags.getText().toString();
		if (!searchTerms.isEmpty()){
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			new LoadRequestSearch().execute(searchTerms, editTags.getText().toString());
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
			searchTags = editTags.getText().toString();
			if (!searchTerms.isEmpty()){
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
				new LoadRequestSearch().execute(searchTerms, editTags.getText().toString());
			}
			else {
				Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
				editTerms.requestFocus();
			}
		}
		return true;
	}

	/**
	 * Load the first page of request search results, params should be { terms, tags }
	 */
	private class LoadRequestSearch extends AsyncTask<String, Void, RequestsSearch> {

		@Override
		protected void onPreExecute(){
			if (footer != null){
				footer.setVisibility(View.VISIBLE);
			}
			if (resultsAdapter != null){
				resultsAdapter.clearSearch();
			}
			requestSearch = null;
		}

		@Override
		protected RequestsSearch doInBackground(String... params){
			try {
				RequestsSearch s = RequestsSearch.search(params[0], params[1]);
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
		protected void onPostExecute(RequestsSearch search){
			if (search != null){
				requestSearch = search;
				if (resultsAdapter != null){
					resultsAdapter.viewSearch(requestSearch);
				}
				if (footer != null && !requestSearch.hasNextPage()){
					footer.setVisibility(View.GONE);
				}
			}
			else {
				Toast.makeText(getActivity(), "Failed to load search results", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
