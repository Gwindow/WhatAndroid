package what.whatandroid.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import what.whatandroid.R;

/**
 * Fragment for searching for torrents
 */
public class TorrentSearchFragment extends Fragment implements View.OnClickListener {
	/**
	 * Search terms and tags sent to use by the intent
	 */
	String intentTerms, intentTags;
	/**
	 * The search input boxes
	 */
	EditText searchTerms, searchTags;
	/**
	 * The list of search results and the search result adapter
	 */
	private ListView resultsList;
	private TorrentSearchAdapter resultsAdapter;

	/**
	 * Create a torrent search fragment and have it start loading the search desired when the view
	 * is created
	 * @param terms terms to search for
	 * @param tags tags to search for
	 * @return a torrent search fragment that will load the desired search
	 */
	public static TorrentSearchFragment newInstance(String terms, String tags){
		TorrentSearchFragment f = new TorrentSearchFragment();
		f.intentTerms = terms;
		f.intentTags = tags;
		return f;
	}

	public TorrentSearchFragment(){
		//required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		resultsList = (ListView)view.findViewById(R.id.list);

		View header = inflater.inflate(R.layout.header_search, null);
		searchTerms = (EditText)header.findViewById(R.id.search_terms);
		searchTags = (EditText)header.findViewById(R.id.search_tags);
		Button search = (Button)header.findViewById(R.id.search_button);
		search.setOnClickListener(this);
		View footer = inflater.inflate(R.layout.footer_loading_indicator, null);
		resultsList.addHeaderView(header);
		resultsList.addFooterView(footer);
		//The footer should not be visible initially, only shown when loading results
		footer.setVisibility(View.GONE);

		resultsAdapter = new TorrentSearchAdapter(getActivity(), footer);
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		resultsList.setOnScrollListener(resultsAdapter);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		//If we were sent a search to run from the intent then start loading it
		if (intentTerms != null){
			if (intentTags == null){
				intentTags = "";
			}
			searchTerms.setText(intentTerms);
			searchTags.setText(intentTags);
			resultsAdapter.viewSearch(intentTerms, intentTags);
		}
	}

	@Override
	public void onClick(View v){
		String terms = searchTerms.getText().toString();
		if (terms.length() != 0){
			resultsAdapter.viewSearch(terms, searchTags.getText().toString());
		}
		else {
			Toast.makeText(getActivity(), "Please enter search terms", Toast.LENGTH_SHORT).show();
		}
	}
}
