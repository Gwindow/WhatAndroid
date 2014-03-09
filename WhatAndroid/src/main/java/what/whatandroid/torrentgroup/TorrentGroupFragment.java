package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import api.torrents.torrents.Artists;
import api.torrents.torrents.Edition;
import api.torrents.torrents.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewArtistCallbacks;

import java.util.List;

/**
 * Fragment for viewing a torrent group's information
 */
public class TorrentGroupFragment extends Fragment {
	/**
	 * The torrent group being viewed
	 */
	private TorrentGroup group;
	/**
	 * The torrent group id, passed to us when creating the fragment so we can load
	 * the group later, when the fragment is actually created
	 */
	private int groupID;
	/**
	 * Callbacks to the parent activity for setting the title or viewing the artist of the group
	 * TODO: How to display/handle multiple artists?
	 */
	private ViewArtistCallbacks callbacks;
	/**
	 * Various content views displaying the group information
	 */
	private ImageView image;
	private View header, artistHeader;
	//Later artist should be a list of artists
	private TextView albumTitle, artist;
	private ExpandableListView torrentList;

	/**
	 * Use this factory method to create a new torrent group fragment displaying the
	 * torrents in the group with id passed
	 *
	 * @param id torrent group id to show
	 * @return TorrentGroupFragment displaying the group
	 */
	public static TorrentGroupFragment newInstance(int id){
		TorrentGroupFragment fragment = new TorrentGroupFragment();
		fragment.groupID = id;
		return fragment;
	}

	public TorrentGroupFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (ViewArtistCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewArtistCallbacks");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (group == null){
			new LoadGroup().execute(groupID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, null);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);

		artistHeader = inflater.inflate(R.layout.header_album_artists, null);
		albumTitle = (TextView)artistHeader.findViewById(R.id.title);
		artist = (TextView)artistHeader.findViewById(R.id.artist);

		header = inflater.inflate(R.layout.header_image, null);
		image = (ImageView)header.findViewById(R.id.image);
		return view;
	}

	/**
	 * Update all the torrent group information being shown after loading
	 */
	private void updateTorrentGroup(List<Edition> editions){
		callbacks.setTitle(group.getResponse().getGroup().getName());

		if (!group.getResponse().getGroup().getWikiImage().equalsIgnoreCase("")){
			ImageLoader.getInstance().displayImage(group.getResponse().getGroup().getWikiImage(), image);
			torrentList.addHeaderView(header);
		}
		else {
			image.setVisibility(View.GONE);
			image = null;
			header = null;
		}

		albumTitle.setText(group.getResponse().getGroup().getName());
		//TODO: move artists into the first entry in the expandable list view
		artist.setText(group.getResponse().getGroup().getMusicInfo().getArtists().get(0).getName());
		artist.setOnClickListener(
			new ArtistClickListener(group.getResponse().getGroup().getMusicInfo().getArtists().get(0)));
		torrentList.addHeaderView(artistHeader);

		torrentList.setAdapter(new TorrentGroupAdapter(getActivity(), editions));
	}

	/**
	 * Async task to load the torrent group info
	 */
	private class LoadGroup extends AsyncTask<Integer, Void, TorrentGroup> {
		List<Edition> editions;
		/**
		 * Load some torrent group from its id
		 *
		 * @param params params[0] should contain the id to load
		 * @return the loaded group, or null if something went wrong
		 */
		@Override
		protected TorrentGroup doInBackground(Integer... params){
			try {
				TorrentGroup tg = TorrentGroup.torrentGroupFromId(params[0]);
				if (tg != null && tg.getStatus()){
					editions = tg.getEditions();
					return tg;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(TorrentGroup torrentGroup){
			if (torrentGroup != null){
				group = torrentGroup;
				updateTorrentGroup(editions);
			}
			//Else show an error?
		}
	}

	/**
	 * Click listener for the artists in the header so that they can be viewed
	 */
	private class ArtistClickListener implements View.OnClickListener {
		/**
		 * The artist to go view
		 */
		private Artists artist;

		/**
		 * Create the listener and set the artist to open when it's clicked
		 * @param a artist to view when clicked
		 */
		public ArtistClickListener(Artists a){
			artist = a;
		}

		@Override
		public void onClick(View v){
			callbacks.viewArtist(artist.getId().intValue());
		}
	}
}
