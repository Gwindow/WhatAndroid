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
	 * The search input boxes
	 */
	EditText searchTerms, searchTags;
	/**
	 * The list of search results and the search result adapter
	 */
	private ListView resultsList;
	private TorrentSearchAdapter resultsAdapter;

	/**
	 * There's no extra setup needed for the torrent search fragment so use this to
	 * create a new one
	 */
	public TorrentSearchFragment(){
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

		resultsList.addHeaderView(header);
		resultsAdapter = new TorrentSearchAdapter(getActivity());
		resultsList.setAdapter(resultsAdapter);
		resultsList.setOnItemClickListener(resultsAdapter);
		return view;
	}

	@Override
	public void onClick(View v){
		String terms = searchTerms.getText().toString();
		//TODO: API level warning?
		if (terms.length() != 0){
			resultsAdapter.viewSearch(terms, searchTags.getText().toString());
		}
		else {
			Toast.makeText(getActivity(), "Please enter search terms", Toast.LENGTH_SHORT).show();
		}
	}
}
