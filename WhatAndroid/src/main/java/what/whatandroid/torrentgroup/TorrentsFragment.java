package what.whatandroid.torrentgroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.torrents.torrents.TorrentGroup;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;

import java.util.List;

/**
 * Fragment that contains the swipe view of the torrents
 */
public class TorrentsFragment extends Fragment implements LoadingListener<TorrentGroup> {
	private static final String TORRENT_IDX = "what.whatandroid.TORRENT_IDX";
	private TorrentPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private TorrentGroup torrentGroup;

	public static TorrentsFragment newInstance(int torrent){
		TorrentsFragment f = new TorrentsFragment();
		Bundle args = new Bundle();
		args.putInt(TorrentGroupActivity.TORRENT_ID, torrent);
		f.setArguments(args);
		return f;
	}

	public TorrentsFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		System.out.println("Creating view");
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		pagerAdapter = new TorrentPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(pagerAdapter);

		if (torrentGroup != null){
			System.out.println("Already loaded before view creation");
			populateAdapter();
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//We sneak this in through the arguments instead so we can access it in populate view easily
		//instead of worrying about not having access to the saved state
		getArguments().putInt(TORRENT_IDX, viewPager.getCurrentItem());
	}

	@Override
	public void onLoadingComplete(TorrentGroup data){
		System.out.println("Torrents fragment loading complete");
		if (torrentGroup == null){
			torrentGroup = data;
			if (pagerAdapter != null){
				populateAdapter();
			}
		}
	}

	/**
	 * Populate the adapter with the torrents. The saved state is used to reselect the
	 * torrent that was being viewed before we changed orientation. If null is passed
	 * then we pick from the arguments and default to the first torrent
	 */
	private void populateAdapter(){
		pagerAdapter.onLoadingComplete(torrentGroup);
		pagerAdapter.notifyDataSetChanged();

		if (getArguments().getInt(TORRENT_IDX, -1) != -1){
			System.out.println("Restoring view of torrent @ " + getArguments().getInt(TORRENT_IDX));
			viewPager.setCurrentItem(getArguments().getInt(TORRENT_IDX));
		}
		else {
			//Lookup which torrent we're viewing (the largest torrent groups are ~100 torrents so this gross but ok)
			//Perhaps later we could track the index in the list with the torrent. Default to first if no id given
			List<Torrents> torrents = torrentGroup.getResponse().getTorrents();

			int idx, torrentId = getArguments().getInt(TorrentGroupActivity.TORRENT_ID, torrents.get(0).getId().intValue());
			System.out.println("Looking for torrent with id " + torrentId);

			for (idx = 0; idx < torrents.size() && torrents.get(idx).getId().intValue() != torrentId; ++idx){
				System.out.println("Compared: " + torrents.get(idx).getId().intValue() + " and " + torrentId);
			}
			System.out.println("Viewing torrent @ " + idx);
			viewPager.setCurrentItem(idx);
		}
	}
}
