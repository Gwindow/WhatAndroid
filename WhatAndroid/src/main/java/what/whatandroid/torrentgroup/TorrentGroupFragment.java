package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.torrents.torrents.Artist;
import api.torrents.torrents.EditionTorrents;
import api.torrents.torrents.MusicInfo;
import api.torrents.torrents.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.callbacks.ViewArtistCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.views.ImageDialog;

import java.util.List;

/**
 * Fragment for viewing a torrent group's information
 */
public class TorrentGroupFragment extends Fragment implements OnLoggedInCallback, View.OnClickListener {
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
	 * Callbacks to the parent activity for setting the title and viewing artists
	 */
	private SetTitleCallback setTitle;
	private ViewArtistCallbacks viewArtist;
	/**
	 * Task used to load the group so we can cancel if we navigate away
	 */
	private LoadGroup loadGroup;
	/**
	 * Various content views displaying the group information
	 * artistA and artistB are used to show and hide artists if there were one or two artists
	 * on the album. If there's 3+ we show various artists in A and show the listing of artists
	 */
	private ImageView image;
	private View imageHeader;
	private TextView artistA, artistB;
	private ProgressBar spinner;
	private TextView albumTitle;
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

	public static TorrentGroupFragment newInstance(TorrentGroup group){
		TorrentGroupFragment fragment = new TorrentGroupFragment();
		fragment.group = group;
		fragment.groupID = group.getId();
		return fragment;
	}

	public TorrentGroupFragment(){
		//Required empty ctor
	}

	public TorrentGroup getGroup(){
		return group;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
			viewArtist = (ViewArtistCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitle and ViewArtist callbacks");
		}
	}

	@Override
	public void onDetach(){
		super.onDetach();
		//Cancel the loading task if it's running
		if (loadGroup != null){
			loadGroup.cancel(true);
		}
	}

	@Override
	public void onLoggedIn(){
		if (group == null){
			loadGroup = new LoadGroup();
			loadGroup.execute(groupID);
		}
		else {
			updateTorrentGroup(group.getEditions());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);

		imageHeader = inflater.inflate(R.layout.header_image, null);
		image = (ImageView)imageHeader.findViewById(R.id.image);
		spinner = (ProgressBar)imageHeader.findViewById(R.id.loading_indicator);
		image.setOnClickListener(this);

		View titleHeader = inflater.inflate(R.layout.header_album_title, null);
		artistA = (TextView)titleHeader.findViewById(R.id.artist_a);
		artistB = (TextView)titleHeader.findViewById(R.id.artist_b);
		albumTitle = (TextView)titleHeader.findViewById(R.id.title);

		torrentList.addHeaderView(imageHeader);
		torrentList.addHeaderView(titleHeader);
		return view;
	}

	/**
	 * When the image in the header is clicked toggle expand/hide on it
	 */
	@Override
	public void onClick(View v){
		if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(group.getResponse().getGroup().getWikiImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
		else if (v.getId() == R.id.artist_a){
			viewArtist.viewArtist(group.getResponse().getGroup().getMusicInfo().getArtists().get(0).getId().intValue());
		}
		else if (v.getId() == R.id.artist_b){
			viewArtist.viewArtist(group.getResponse().getGroup().getMusicInfo().getArtists().get(1).getId().intValue());
		}
	}

	/**
	 * Update all the torrent group information being shown after loading
	 */
	private void updateTorrentGroup(List<EditionTorrents> editions){
		setTitle.setTitle(group.getResponse().getGroup().getName());

		String imgUrl = group.getResponse().getGroup().getWikiImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner));
		}
		else {
			imageHeader.setVisibility(View.GONE);
		}
		albumTitle.setText(group.getResponse().getGroup().getName());

		//Choose the names for ArtistA and ArtistB or hide entirely depending on the number of artists
		TorrentGroupAdapter adapter;
		MusicInfo musicInfo = group.getResponse().getGroup().getMusicInfo();
		if (musicInfo == null || musicInfo.getArtists().size() > 2 || musicInfo.getArtists().isEmpty()){
			adapter = new TorrentGroupAdapter(getActivity(), getChildFragmentManager(), musicInfo, editions);
			artistA.setText("Various Artists");
			artistB.setVisibility(View.GONE);
		}
		else {
			artistA.setOnClickListener(this);
			artistB.setOnClickListener(this);
			List<Artist> artists = musicInfo.getAllArtists();
			if (musicInfo.getArtists().size() == 2){
				artistA.setText(musicInfo.getArtists().get(0).getName());
				artistB.setText(musicInfo.getArtists().get(1).getName());
				adapter = new TorrentGroupAdapter(getActivity(), getChildFragmentManager(),
					artists.subList(2, artists.size()), editions);
			}
			else {
				artistA.setText(musicInfo.getArtists().get(0).getName());
				artistB.setVisibility(View.GONE);
				adapter = new TorrentGroupAdapter(getActivity(), getChildFragmentManager(),
					artists.subList(1, artists.size()), editions);
			}
		}
		torrentList.setAdapter(adapter);
		torrentList.setOnChildClickListener(adapter);
	}

	/**
	 * Async task to load the torrent group info
	 */
	private class LoadGroup extends AsyncTask<Integer, Void, TorrentGroup> {
		List<EditionTorrents> editions;

		/**
		 * Load some torrent group from its id
		 *
		 * @param params params[0] should contain the id to load
		 * @return the loaded group, or null if something went wrong
		 */
		@Override
		protected TorrentGroup doInBackground(Integer... params){
			try {
				TorrentGroup tg = TorrentGroup.fromId(params[0]);
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
		protected void onPreExecute(){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(true);
				getActivity().setProgressBarIndeterminate(true);
			}
		}

		@Override
		protected void onPostExecute(TorrentGroup torrentGroup){
			getActivity().setProgressBarIndeterminateVisibility(false);
			getActivity().setProgressBarIndeterminate(false);
			if (torrentGroup != null){
				group = torrentGroup;
				updateTorrentGroup(editions);
			}
			else {
				Toast.makeText(getActivity(), "Failed to load torrent group", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
