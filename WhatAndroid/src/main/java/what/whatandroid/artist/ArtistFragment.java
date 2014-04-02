package what.whatandroid.artist;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import api.torrents.artist.Artist;
import api.torrents.artist.Releases;
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
public class ArtistFragment extends Fragment implements OnLoggedInCallback, View.OnClickListener {
	/**
	 * The artist being viewed
	 */
	private Artist artist;
	/**
	 * The artists releases
	 */
	private Releases releases;
	/**
	 * The artist id, passed to us on fragment creation so we can load the artist later
	 */
	private int artistID;
	/**
	 * Callbacks to the activity so we can set the title
	 */
	private SetTitleCallback callbacks;
	/**
	 * Various content views displaying the artist information
	 */
	private ImageView image;
	private ProgressBar spinner;
	private View header;
	private ExpandableListView torrentList;

	/**
	 * Use this factory method to create a new artist fragment displaying information about
	 * the artist with the id
	 * @param id artist id to show
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(int id){
		ArtistFragment f = new ArtistFragment();
		f.artistID = id;
		return f;
	}

	/**
	 * Use this factory method to create a new artist fragment displaying information about
	 * an already loaded artist
	 * @param a the artist to view
	 * @param r the releases for the artist
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(Artist a, Releases r){
		ArtistFragment f = new ArtistFragment();
		f.artist = a;
		f.artistID = a.getId();
		f.releases = r;
		return f;
	}

	public ArtistFragment(){
		//Required empty public ctor
	}

	public Artist getArtist(){
		return artist;
	}

	public int getArtistID(){
		return artistID;
	}

	@Override
	public void onLoggedIn(){
		if (artist == null){
			new LoadArtist().execute(artistID);
		}
		else if (releases == null){
			releases = new Releases(artist);
		}
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
		header = inflater.inflate(R.layout.header_image, null);
		torrentList.addHeaderView(header);
		image = (ImageView)header.findViewById(R.id.image);
		image.setOnClickListener(this);
		spinner = (ProgressBar)header.findViewById(R.id.loading_indicator);
		if (artist != null){
			updateArtist();
		}
		return view;
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(artist.getResponse().getImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
	}

	/**
	 * Update all the artist information with the loaded api request
	 */
	private void updateArtist(){
		callbacks.setTitle(artist.getResponse().getName());
		String imgUrl = artist.getResponse().getImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner));
		}
		else {
			image.setVisibility(View.GONE);
			spinner.setVisibility(View.GONE);
		}
		//Don't initialize if we're moving away from the view
		if (getActivity() != null){
			ArtistTorrentAdapter adapter = new ArtistTorrentAdapter(getActivity(), releases.flatten(),
				artist.getResponse().getRequests());
			torrentList.setAdapter(adapter);
			torrentList.setOnChildClickListener(adapter);
		}
	}

	/**
	 * Async task to load the artist info
	 */
	private class LoadArtist extends AsyncTask<Integer, Void, Artist> {
		/**
		 * Load some torrent artist from their id
		 *
		 * @param params params[0] should contain the artist id to load
		 * @return the loaded artist, or null if something went wrong
		 */
		@Override
		protected Artist doInBackground(Integer... params){
			try {
				Artist a = Artist.fromId(params[0]);
				if (a != null && a.getStatus()){
					releases = new Releases(a);
					return a;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute(){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(true);
				getActivity().setProgressBarIndeterminate(true);
			}
		}

		@Override
		protected void onPostExecute(Artist a){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(false);
				getActivity().setProgressBarIndeterminate(false);
			}
			if (a != null){
				artist = a;
				updateArtist();
			}
			else {
				Toast.makeText(getActivity(), "Failed to load artist", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
