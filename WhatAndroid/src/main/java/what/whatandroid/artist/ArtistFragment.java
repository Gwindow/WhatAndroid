package what.whatandroid.artist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import api.torrents.artist.Artist;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.views.ImageDialog;

/**
 * Fragment for viewing an artist's information and torrent groups
 */
public class ArtistFragment extends Fragment implements OnLoggedInCallback, View.OnClickListener,
	LoaderManager.LoaderCallbacks<Artist> {
	/**
	 * The artist being viewed
	 */
	private Artist artist;
	/**
	 * Callbacks to the activity so we can set the title
	 */
	private SetTitleCallback callbacks;
	/**
	 * Various content views displaying the artist information
	 */
	private ImageView image;
	private ProgressBar spinner;
	private ExpandableListView torrentList;

	/**
	 * Use this factory method to create a new artist fragment displaying information about
	 * the artist with the id
	 *
	 * @param id        artist id to load
	 * @param name        artist name to load
	 * @param useSearch true if the artist information was loaded by the ArtistSearch fragment
	 *                  and we should get it from there
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(int id, String name, boolean useSearch){
		ArtistFragment f = new ArtistFragment();
		Bundle args = new Bundle();
		args.putInt(ArtistActivity.ARTIST_ID, id);
		args.putString(ArtistActivity.ARTIST_NAME, name);
		args.putBoolean(ArtistActivity.USE_SEARCH, useSearch);
		f.setArguments(args);
		return f;
	}

	public ArtistFragment(){
		//Required empty public ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewTorrentCallbacks!");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);
		View header = inflater.inflate(R.layout.header_image, null);
		torrentList.addHeaderView(header);
		image = (ImageView)header.findViewById(R.id.image);
		image.setOnClickListener(this);
		spinner = (ProgressBar)header.findViewById(R.id.loading_indicator);
		if (artist != null){
			populateViews();
		}
		return view;
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(artist.getResponse().getImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
	}

	@Override
	public Loader<Artist> onCreateLoader(int id, Bundle args){
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		return new ArtistAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Artist> loader, Artist data){
		artist = data;
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(false);
			getActivity().setProgressBarIndeterminateVisibility(false);
			if (artist != null && artist.getStatus()){
				populateViews();
			}
			else {
				Toast.makeText(getActivity(), "Could not load artist", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Artist> loader){
	}

	/**
	 * Update all the artist information with the loaded api request
	 */
	private void populateViews(){
		callbacks.setTitle(artist.getResponse().getName());
		String imgUrl = artist.getResponse().getImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner));
		}
		else {
			image.setVisibility(View.GONE);
			spinner.setVisibility(View.GONE);
		}
		ArtistTorrentAdapter adapter = new ArtistTorrentAdapter(getActivity(), artist.getReleases().flatten(),
			artist.getResponse().getRequests());
		torrentList.setAdapter(adapter);
		torrentList.setOnChildClickListener(adapter);
	}
}
