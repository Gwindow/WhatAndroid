package what.whatandroid.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import api.torrents.artist.Artist;
import what.whatandroid.R;
import what.whatandroid.artist.ArtistActivity;
import what.whatandroid.artist.ArtistAsyncLoader;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for searching for artists. If only one artist name is returned from the search
 * we view that artist, if multiple ones are returned we go to a torrent search with the search term
 */
public class ArtistSearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener,
	OnLoggedInCallback, LoaderManager.LoaderCallbacks<Artist> {
	/**
	 * So we can set the action bar title
	 */
	private SetTitleCallback setTitle;
	/**
	 * Search terms sent to us by the intent
	 */
	private String searchTerms;
	/**
	 * The loaded artist if we found one, this is used so that the Artist Activity can pick up
	 * the loaded artist without having to re-download it
	 */
	private static Artist artist;
	/**
	 * The search input box and loading indicator
	 */
	private EditText editTerms;
	private ProgressBar loadingIndicator;

	/**
	 * Create an artist search fragment and have it start loading the search desired when the view
	 * is resumed. If the terms are empty then no search will be launched at load
	 *
	 * @param terms terms to run search with. If empty no search will be launched
	 * @return Artist Search fragment viewing the search results, or ready to take input
	 */
	public static ArtistSearchFragment newInstance(String terms){
		ArtistSearchFragment f = new ArtistSearchFragment();
		Bundle args = new Bundle();
		args.putString(SearchActivity.TERMS, terms);
		f.setArguments(args);
		return f;
	}

	public ArtistSearchFragment(){
		//Required empty ctor
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
		searchTerms = getArguments().getString(SearchActivity.TERMS, "");
	}

	@Override
	public void onLoggedIn(){
		//Artist search fragment really shouldn't auto-search, since it only re-directs to new fragments
		//based on the result
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		setTitle.setTitle("Artist Search");
		View view = inflater.inflate(R.layout.fragment_artist_search, container, false);
		Button searchButton = (Button)view.findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		editTerms = (EditText)view.findViewById(R.id.search_terms);
		editTerms.setOnEditorActionListener(this);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		editTerms.setText(searchTerms);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//We just update the arguments instead since we treat them the same
		getArguments().putString(SearchActivity.TERMS, searchTerms);
	}

	@Override
	public void onClick(View v){
		searchTerms = editTerms.getText().toString();
		if (!searchTerms.isEmpty()){
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromInputMethod(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			Bundle args = new Bundle();
			args.putString(ArtistActivity.ARTIST_NAME, searchTerms);
			getLoaderManager().restartLoader(0, args, this);
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
				imm.hideSoftInputFromInputMethod(editTerms.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
				Bundle args = new Bundle();
				args.putString(ArtistActivity.ARTIST_NAME, searchTerms);
				getLoaderManager().restartLoader(0, args, this);
			}
			else {
				Toast.makeText(getActivity(), "Enter search terms", Toast.LENGTH_SHORT).show();
				editTerms.requestFocus();
			}
		}
		return true;
	}

	/**
	 * Get the loaded artist from the search
	 *
	 * @return the loaded artist from the search
	 */
	public static Artist getArtist(){
		return artist;
	}


	@Override
	public Loader<Artist> onCreateLoader(int id, Bundle args){
		if (isAdded()){
			loadingIndicator.setVisibility(View.VISIBLE);
		}
		return new ArtistAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Artist> loader, Artist data){
		artist = data;
		if (isAdded()){
			loadingIndicator.setVisibility(View.GONE);
			//If we found the artist then go to the artist's page, otherwise launch a torrent search with the terms
			if (artist != null && artist.getStatus()){
				Intent intent = new Intent(getActivity(), ArtistActivity.class);
				intent.putExtra(ArtistActivity.ARTIST_ID, artist.getId());
				intent.putExtra(ArtistActivity.USE_SEARCH, true);
				startActivity(intent);
			}
			else {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
				intent.putExtra(SearchActivity.TERMS, searchTerms);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Artist> loader){

	}
}
