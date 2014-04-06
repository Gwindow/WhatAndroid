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
import api.search.torrents.TorrentSearch;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for searching for torrents
 */
public class TorrentSearchFragment extends Fragment
	implements View.OnClickListener, TextView.OnEditorActionListener, OnLoggedInCallback {
	/**
	 * Search terms and tags sent to use by the intent
	 */
	private String searchTerms, searchTags;
	/**
	 * The torrent search we're viewing
	 */
	private TorrentSearch torrentSearch;
	private LoadTorrentSearch loadTorrentSearch;
	/**
	 * The search input boxes
	 */
	private EditText editTerms, editTags;
	/**
	 * The list of search results and the search result adapter
	 */
	private TorrentSearchAdapter resultsAdapter;
	/**
	 * The loading status footer
	 */
	private View footer;
	private TextView noResults;

	/**
	 * Create a torrent search fragment and have it start loading the search desired when the view
	 * is created. If the terms are empty then no search will be launched
	 * @param terms terms to search for
	 * @param tags tags to search for
	 * @return a torrent search fragment that will load the desired search
	 */
	public static TorrentSearchFragment newInstance(String terms, String tags){
		TorrentSearchFragment f = new TorrentSearchFragment();
		f.searchTerms = terms;
		f.searchTags = tags;
		return f;
	}

	public TorrentSearchFragment(){
		//required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			SetTitleCallback callback = (SetTitleCallback)activity;
			callback.setTitle("Torrent Search");
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallback");
		}
	}

	@Override
	public void onDetach(){
		super.onDetach();
		if (loadTorrentSearch != null){
			loadTorrentSearch.cancel(true);
		}
	}

	@Override
	public void onLoggedIn(){
		//If we were sent a search to load from the intent, start loading it
		if (searchTerms != null && torrentSearch == null){
			if (searchTags == null){
				searchTags = "";
			}
			if (loadTorrentSearch != null){
				loadTorrentSearch.cancel(true);
			}
			loadTorrentSearch = new LoadTorrentSearch();
			loadTorrentSearch.execute(searchTerms, searchTags);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView resultsList = (ListView)view.findViewById(R.id.list);
		noResults = (TextView)view.findViewById(R.id.no_content_notice);
		noResults.setText("No Results");

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

		resultsAdapter = new TorrentSearchAdapter(getActivity(), footer);
		if (torrentSearch != null){
			resultsAdapter.viewSearch(torrentSearch);
		}
		if (searchTerms == null || searchTerms.isEmpty()){
			editTerms.requestFocus();
		}
		else {
			editTerms.setText(searchTerms);
			editTags.setText(searchTags);
			if (torrentSearch == null){
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
			if (loadTorrentSearch != null){
				loadTorrentSearch.cancel(true);
			}
			loadTorrentSearch = new LoadTorrentSearch();
			loadTorrentSearch.execute(searchTerms, searchTags);
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
				if (loadTorrentSearch != null){
					loadTorrentSearch.cancel(true);
				}
				loadTorrentSearch = new LoadTorrentSearch();
				loadTorrentSearch.execute(searchTerms, searchTags);
			}
			else {
				Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
				editTerms.requestFocus();
			}
		}
		return true;
	}

	/**
	 * Load the first page of torrent search results in the background, params should be { terms, tags }
	 */
	private class LoadTorrentSearch extends AsyncTask<String, Void, TorrentSearch> {
		@Override
		protected void onPreExecute(){
			if (footer != null){
				footer.setVisibility(View.VISIBLE);
			}
			if (resultsAdapter != null){
				resultsAdapter.clearSearch();
			}
			torrentSearch = null;
		}

		@Override
		protected TorrentSearch doInBackground(String... params){
			try {
				TorrentSearch s = TorrentSearch.search(params[0], params[1]);
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
		protected void onPostExecute(TorrentSearch search){
			if (search != null){
				torrentSearch = search;
				if (resultsAdapter != null){
					if (torrentSearch.getResponse().getResults().isEmpty()){
						noResults.setVisibility(View.VISIBLE);
					}
					else {
						noResults.setVisibility(View.GONE);
					}
					resultsAdapter.viewSearch(torrentSearch);
				}
				if (footer != null && !torrentSearch.hasNextPage()){
					footer.setVisibility(View.GONE);
				}
			}
			else {
				Toast.makeText(getActivity(), "Failed to load search results", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
