package what.whatandroid.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import api.user.recent.RecentTorrent;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 * Fragment for displaying information about a recently uploaded
 * or snatched torrent
 */
public class RecentTorrentFragment extends Fragment implements View.OnClickListener {
	/**
	 * The torrent we're displaying
	 */
	private RecentTorrent torrent;
	/**
	 * Callbacks to let us go view a recent torrent
	 */
	private ViewTorrentCallbacks callbacks;

	/**
	 * Create a new RecentTorrentFragment to display information about the torrent
	 *
	 * @param t Torrent to display
	 * @return A RecentTorrentFragment displaying the torrent
	 */
	public static RecentTorrentFragment newInstance(RecentTorrent t){
		RecentTorrentFragment fragment = new RecentTorrentFragment();
		fragment.torrent = t;
		return fragment;
	}

	public RecentTorrentFragment(){
		//Required empty public ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recent_torrent, container, false);
		ImageView art = (ImageView)view.findViewById(R.id.art);
		if (torrent != null){
			ImageLoader.getInstance().displayImage(torrent.getWikiImage(), art);
			TextView albumName = (TextView)view.findViewById(R.id.album_name);
			albumName.setText(torrent.getName());
			TextView artistName = (TextView)view.findViewById(R.id.artist_name);
			artistName.setText(torrent.getArtist().getName());
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
		callbacks.viewTorrentGroup(torrent.getID());
	}
}
