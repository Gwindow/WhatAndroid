package what.whatandroid.search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import api.torrents.artist.Artist;
import what.whatandroid.R;
import what.whatandroid.artist.ArtistActivity;

/**
 * Fragment for searching for artists. If only one artist name is returned from the search
 * we view that artist, if multiple ones are returned we go to a torrent search with the search term
 */
public class ArtistSearchFragment extends Fragment implements View.OnClickListener {
	/**
	 * Search terms sent to us by the intent
	 */
	private String intentTerms;
	/**
	 * The loaded artist if we found one, this is used so that the Artist Activity can pick up
	 * the loaded artist without having to re-download it
	 */
	private static Artist artist;
	/**
	 * The search input box and loading indicator
	 */
	private EditText searchTerms;
	private ProgressBar loadingIndicator;

	/**
	 * Create an artist search fragment and have it start loading the search desired when the view
	 * is resumed. If the terms are empty then no search will be launched at load
	 * @param terms terms to run search with. If empty no search will be launched
	 * @return Artist Search fragment viewing the search results, or ready to take input
	 */
	public static ArtistSearchFragment newInstance(String terms){
		ArtistSearchFragment f = new ArtistSearchFragment();
		f.intentTerms = terms;
		return f;
	}

	public ArtistSearchFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//If we were sent a search to run from the intent start loading it
		if (intentTerms != null){
			new LoadArtistSearch().execute(intentTerms);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_artist_search, container, false);
		Button searchButton = (Button)view.findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		searchTerms = (EditText)view.findViewById(R.id.search_terms);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		//If we're not loading some search from an intent
		if (intentTerms == null){
			loadingIndicator.setVisibility(View.GONE);
		}
		else {
			searchTerms.setText(intentTerms);
		}
		return view;
	}

	@Override
	public void onClick(View v){
		String terms = searchTerms.getText().toString();
		if (terms.length() > 0){
			new LoadArtistSearch().execute(terms);
		}
		else {
			Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Get the loaded artist from the search
	 * @return the loaded artist from the search
	 */
	public static Artist getArtist(){
		return artist;
	}

	/**
	 * Load the artist auto completions for some artist name as a "search". If only one completion
	 * is returned we launch an intent to view that artist, if multiple completions are returned we
	 * launch a torrent search with the same terms, as the site does.
	 */
	private class LoadArtistSearch extends AsyncTask<String, Void, Artist> {
		/**
		 * Artist name we're trying to load
		 */
		String name;

		@Override
		protected void onPreExecute(){
			if (loadingIndicator != null){
				loadingIndicator.setVisibility(View.VISIBLE);
			}
			artist = null;
		}

		@Override
		protected Artist doInBackground(String... params){
			name = params[0];
			try {
				Artist a = Artist.fromName(params[0]);
				if (a != null){
					return a;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Artist a){
			if (loadingIndicator != null){
				loadingIndicator.setVisibility(View.GONE);
			}
			if (a != null){
				artist = a;
				Intent intent = new Intent(getActivity(), ArtistActivity.class);
				intent.putExtra(ArtistActivity.ARTIST_ID, artist.getId());
				intent.putExtra(ArtistActivity.USE_SEARCH, true);
				startActivity(intent);
			}
			else {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
				intent.putExtra(SearchActivity.TERMS, name);
				startActivity(intent);
			}
		}
	}
}
