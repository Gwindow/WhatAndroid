package what.whatandroid.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.user.recent.RecentTorrent;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * Fragment for displaying information about a recently uploaded
 * or snatched torrent
 */
public class RecentTorrentFragment extends Fragment implements View.OnClickListener, LoadingListener<RecentTorrent> {
	/**
	 * The torrent we're displaying. We track the torrent id separately to serialize it in a bundle
	 */
	private RecentTorrent torrent;
	private int torrentId;
	/**
	 * Callbacks to let us go view a recent torrent
	 */
	private ViewTorrentCallbacks callbacks;
	/**
	 * Views displaying the torrent information
	 */
	private ImageView art;
	private ProgressBar spinner;
	private View artContainer;
	private TextView albumName, artistName;

	/**
	 * Create a new RecentTorrentFragment to display information about the torrent
	 *
	 * @param t Torrent to display
	 * @return A RecentTorrentFragment displaying the torrent
	 */
	public static RecentTorrentFragment newInstance(RecentTorrent t){
		RecentTorrentFragment fragment = new RecentTorrentFragment();
		fragment.torrent = t;
		Bundle args = new Bundle();
		args.putInt(TorrentGroupActivity.GROUP_ID, t.getId());
		fragment.setArguments(args);
		return fragment;
	}

	public RecentTorrentFragment(){
		//Required empty public ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		torrentId = getArguments().getInt(TorrentGroupActivity.GROUP_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recent_torrent, container, false);
		art = (ImageView)view.findViewById(R.id.art);
		spinner = (ProgressBar)view.findViewById(R.id.loading_indicator);
		artContainer = view.findViewById(R.id.art_container);
		albumName = (TextView)view.findViewById(R.id.album_name);
		artistName = (TextView)view.findViewById(R.id.artist_name);
		if (torrent != null){
			populateView();
		}
		view.setOnClickListener(this);
		return view;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (ViewTorrentCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	@Override
	public void onClick(View v){
		callbacks.viewTorrentGroup(torrentId);
	}

	@Override
	public void onLoadingComplete(RecentTorrent data){
		torrent = data;
		torrentId = torrent.getId();
		getArguments().putInt(TorrentGroupActivity.GROUP_ID, torrentId);
		populateView();
	}

	private void populateView(){
		//If we've got the views then update them
		if (artistName != null){
			String imgUrl = torrent.getWikiImage();
			if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
				ImageLoader.getInstance().displayImage(torrent.getWikiImage(), art, new ImageLoadingListener(spinner, artContainer));
			}
			else {
				artContainer.setVisibility(View.GONE);
			}
			albumName.setText(torrent.getName());
			//For 3+ artists show Various Artists, for 2 show A & B for 1 just show the artist name
			switch (torrent.getArtists().size()){
				case 1:
					artistName.setText(torrent.getArtists().get(0).getName());
					break;
				case 2:
					artistName.setText(torrent.getArtists().get(0).getName()
						+ " & " + torrent.getArtists().get(1).getName());
					break;
				default:
					artistName.setText("Various Artists");
			}
		}
	}
}

