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
import android.widget.Toast;
import api.torrents.artist.Artist;
import api.torrents.artist.Releases;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for viewing an artist's information and torrent groups
 */
public class ArtistFragment extends Fragment implements OnLoggedInCallback {
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
	 *
	 * @param a the artist to view
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(Artist a){
		ArtistFragment f = new ArtistFragment();
		f.artist = a;
		f.artistID = a.getId();
		return f;
	}

	public ArtistFragment(){
		//Required empty public ctor
	}

	@Override
	public void onLoggedIn(){
		if (artist == null){
			new LoadArtist().execute(artistID);
		}
		else {
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
		image = (ImageView)header.findViewById(R.id.image);
		if (artist != null){
			updateArtist();
		}
		return view;
	}

	/**
	 * Update all the artist information with the loaded api request
	 */
	private void updateArtist(){
		callbacks.setTitle(artist.getResponse().getName());
		if (!artist.getResponse().getImage().equalsIgnoreCase("")){
			ImageLoader.getInstance().displayImage(artist.getResponse().getImage(), image);
			torrentList.addHeaderView(header);
		}
		else {
			image = null;
			header = null;
		}
		ArtistTorrentAdapter adapter = new ArtistTorrentAdapter(getActivity(), releases.flatten());
		torrentList.setAdapter(adapter);
		torrentList.setOnChildClickListener(adapter);
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
			getActivity().setProgressBarIndeterminateVisibility(true);
			getActivity().setProgressBarIndeterminate(true);
		}

		@Override
		protected void onPostExecute(Artist a){
			getActivity().setProgressBarIndeterminateVisibility(false);
			getActivity().setProgressBarIndeterminate(false);
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
