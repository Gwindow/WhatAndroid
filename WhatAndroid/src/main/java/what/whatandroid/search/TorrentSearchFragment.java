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
import api.search.torrents.TorrentSearch;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for searching for torrents
 */
public class TorrentSearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener,
	OnLoggedInCallback, LoaderManager.LoaderCallbacks<TorrentSearch>, AbsListView.OnScrollListener {
	/**
	 * So we can set the action bar title
	 */
	private SetTitleCallback setTitle;
	/**
	 * Search terms and tags sent to use by the intent
	 */
	private String searchTerms, searchTags;
	/**
	 * The torrent search we're viewing
	 */
	private TorrentSearch torrentSearch;
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
	 *
	 * @param terms terms to search for
	 * @param tags  tags to search for
	 * @return a torrent search fragment that will load the desired search
	 */
	public static TorrentSearchFragment newInstance(String terms, String tags){
		TorrentSearchFragment f = new TorrentSearchFragment();
		Bundle args = new Bundle();
		args.putString(SearchActivity.TERMS, terms);
		args.putString(SearchActivity.TAGS, tags);
		f.setArguments(args);
		return f;
	}

	public TorrentSearchFragment(){
		//required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallback");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			searchTerms = getArguments().getString(SearchActivity.TERMS, "");
			searchTags = getArguments().getString(SearchActivity.TAGS, "");
		}
		else {
			searchTerms = savedInstanceState.getString(SearchActivity.TERMS, "");
			searchTags = savedInstanceState.getString(SearchActivity.TAGS, "");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		setTitle.setTitle("Torrent Search");
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
		footer.setVisibility(View.GONE);

		resultsAdapter = new TorrentSearchAdapter(getActivity());
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		resultsList.setOnScrollListener(this);

		if (searchTerms.isEmpty() && searchTags.isEmpty()){
			editTerms.requestFocus();
		}
		else {
			editTerms.setText(searchTerms);
			editTags.setText(searchTags);
			if (MySoup.isLoggedIn()){
				footer.setVisibility(View.VISIBLE);
				startSearch(searchTerms, searchTags, 1);
			}
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(SearchActivity.TERMS, editTerms.getText().toString());
		outState.putString(SearchActivity.TAGS, editTags.getText().toString());
	}

	@Override
	public void onLoggedIn(){
		//If we were sent a search intent and are added then start loading it
		if (!searchTerms.isEmpty() || !searchTags.isEmpty()){
			footer.setVisibility(View.VISIBLE);
			startSearch(searchTerms, searchTags, 1);
		}
	}

	@Override
	public void onClick(View v){
		searchTerms = editTerms.getText().toString();
		searchTags = editTags.getText().toString();
		if (!searchTerms.isEmpty() || !searchTags.isEmpty()){
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			resultsAdapter.clear();
			resultsAdapter.notifyDataSetChanged();
			footer.setVisibility(View.VISIBLE);
			startSearch(searchTerms, searchTags, 1);
		}
		else {
			Toast.makeText(getActivity(), "Enter search terms or tags", Toast.LENGTH_SHORT).show();
			editTerms.requestFocus();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
		if (event == null || event.getAction() == KeyEvent.ACTION_DOWN){
			searchTerms = editTerms.getText().toString();
			searchTags = editTags.getText().toString();
			if (!searchTerms.isEmpty() || !searchTags.isEmpty()){
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
				resultsAdapter.clear();
				resultsAdapter.notifyDataSetChanged();
				footer.setVisibility(View.VISIBLE);
				startSearch(searchTerms, searchTags, 1);
			}
			else {
				Toast.makeText(getActivity(), "Enter search terms or tags", Toast.LENGTH_SHORT).show();
				editTerms.requestFocus();
			}
		}
		return true;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//Load more if we're within 10 items of the end and there's a next page to load
		if (torrentSearch != null && torrentSearch.hasNextPage() && firstVisibleItem + visibleItemCount + 10 >= totalItemCount){
			startSearch(searchTerms, searchTags, torrentSearch.getPage() + 1);
		}
	}

	private void startSearch(String terms, String tags, int page){
		noResults.setVisibility(View.GONE);
		Bundle args = new Bundle();
		args.putString(SearchActivity.TERMS, terms);
		args.putString(SearchActivity.TAGS, tags);
		args.putInt(SearchActivity.PAGE, page);
		LoaderManager lm = getLoaderManager();
		Loader l = lm.getLoader(page);
		if (l == null){
			lm.initLoader(page, args, this);
		}
		//Check if the terms and tags are different from what the loader has and restart if they're different
		else {
			TorrentSearchAsyncLoader s = (TorrentSearchAsyncLoader)l;
			if (!s.getTerms().equalsIgnoreCase(searchTerms) || !s.getTags().equalsIgnoreCase(tags)){
				lm.restartLoader(page, args, this);
			}
			else {
				lm.initLoader(page, args, this);
			}
		}
	}

	@Override
	public Loader<TorrentSearch> onCreateLoader(int id, Bundle args){
		return new TorrentSearchAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<TorrentSearch> loader, TorrentSearch data){
		torrentSearch = data;
		//Update the views like in post execute
		if (torrentSearch != null){
			if (isAdded()){
				if (torrentSearch.getResponse().getResults().isEmpty()){
					noResults.setVisibility(View.VISIBLE);
				}
				//If it's the first page then clear any previous search
				if (torrentSearch.getPage() == 1){
					resultsAdapter.clear();
				}
				resultsAdapter.addAll(torrentSearch.getResponse().getResults());
				resultsAdapter.notifyDataSetChanged();
				if (!torrentSearch.hasNextPage()){
					footer.setVisibility(View.GONE);
				}
			}
		}
		else if (isAdded()){
			Toast.makeText(getActivity(), "Could not load search results", Toast.LENGTH_LONG).show();
			footer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<TorrentSearch> loader){
	}
}
