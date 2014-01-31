package what.whatandroid.artist;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import api.torrents.artist.Artist;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Fragment for viewing an artist's information and torrent groups
 */
public class ArtistFragment extends Fragment {
	/**
	 * The artist being viewed
	 */
	private Artist artist;
	/**
	 * The artist id, passed to us on fragment creation so we can load the artist later
	 */
	private int artistID;
	/**
	 * Callbacks to the activity so we can set the title
	 */
	ViewTorrentCallbacks callbacks;
	/**
	 * Various content views displaying the artist information
	 */
	private ImageView image;
	private View header;
	private ListView torrentList;

	/**
	 * Use this factory method to create a new artist fragment displaying information about
	 * the artist with the id
	 *
	 * @param id artist id to show
	 * @return Artist Fragment displaying the artist's info
	 */
	public static ArtistFragment newInstance(int id){
		ArtistFragment fragment = new ArtistFragment();
		fragment.artistID = id;
		return fragment;
	}

	public ArtistFragment(){
		//Required empty public ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (ViewTorrentCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewTorrentCallbacks!");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (artist == null){
			new LoadArtist().execute(artistID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		header = inflater.inflate(R.layout.header_image, null);
		image = (ImageView)header.findViewById(R.id.image);
		torrentList = (ListView)view.findViewById(R.id.list);
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
			image.setVisibility(View.GONE);
			image = null;
			header = null;
		}
		torrentList.setAdapter(new ArtistTorrentAdapter(getActivity(), R.layout.fragment_artist_torrent,
			artist.getResponse().getTorrentgroup()));
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
			if (a != null){
				artist = a;
				updateArtist();
			}
			//Else show an error?
		}
	}
}
