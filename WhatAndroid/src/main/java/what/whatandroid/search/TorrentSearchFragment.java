package what.whatandroid.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import api.search.torrents.TorrentSearch;
import what.whatandroid.R;

/**
 * Fragment for searching for torrents
 */
public class TorrentSearchFragment extends Fragment implements View.OnClickListener {
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
	private ListView resultsList;
	private TorrentSearchAdapter resultsAdapter;
	/**
	 * The loading status footer
	 */
	private View footer;

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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//If we were sent a search to load from the intent, start loading it
		if (searchTerms != null){
			if (searchTags == null){
				searchTags = "";
			}
			new LoadTorrentSearch().execute(searchTerms, searchTags);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		resultsList = (ListView)view.findViewById(R.id.list);

		View header = inflater.inflate(R.layout.header_search, null);
		editTerms = (EditText)header.findViewById(R.id.search_terms);
		editTags = (EditText)header.findViewById(R.id.search_tags);
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
		//If we're loading a search from an intent fill in the text boxes
		if (searchTerms != null){
			editTerms.setText(searchTerms);
			editTags.setText(searchTags);
			if (torrentSearch == null){
				footer.setVisibility(View.VISIBLE);
			}
		}
		else {
			editTerms.requestFocus();
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
		if (searchTerms.length() > 0){
			new LoadTorrentSearch().execute(searchTerms, editTags.getText().toString());
		}
		else {
			Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Load the first page of torrent search results in the background, params should be { terms, tags }, or
	 * empty if a search has already been loaded and we want to load the next page
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
