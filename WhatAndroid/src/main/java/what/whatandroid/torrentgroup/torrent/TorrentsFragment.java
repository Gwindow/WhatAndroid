package what.whatandroid.torrentgroup.torrent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import api.torrents.torrents.TorrentGroup;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.torrentgroup.DownloadDialog;
import what.whatandroid.torrentgroup.TorrentGroupActivity;

/**
 * Fragment that contains the swipe view of the torrents
 */
public class TorrentsFragment extends Fragment implements LoadingListener<TorrentGroup> {
	private static final String TORRENT_IDX = "what.whatandroid.TORRENT_IDX";
	private SetTitleCallback setTitle;
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
		//We want to show a download icon to download the torrent
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		pagerAdapter = new TorrentPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(pagerAdapter);

		if (torrentGroup != null){
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
		if (torrentGroup == null){
			torrentGroup = data;
			if (pagerAdapter != null){
				populateAdapter();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.torrent_file, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_download && torrentGroup != null){
			Torrents t = torrentGroup.getResponse().getTorrents().get(viewPager.getCurrentItem());
			DownloadDialog dialog = DownloadDialog.newInstance(torrentGroup.getResponse().getGroup().getName(), t);
			dialog.show(getChildFragmentManager(), "download_dialog");
			return true;
		}
		return false;
	}

	/**
	 * Populate the adapter with the torrents. The saved state is used to reselect the
	 * torrent that was being viewed before we changed orientation. If null is passed
	 * then we pick from the arguments and default to the first torrent
	 */
	private void populateAdapter(){
		setTitle.setTitle(torrentGroup.getResponse().getGroup().getName());
		pagerAdapter.onLoadingComplete(torrentGroup);
		pagerAdapter.notifyDataSetChanged();

		if (getArguments().getInt(TORRENT_IDX, -1) != -1){
			viewPager.setCurrentItem(getArguments().getInt(TORRENT_IDX));
		}
		else {
			//Lookup which torrent we're viewing (the largest torrent groups are ~100 torrents so this gross but ok)
			//Perhaps later we could track the index in the list with the torrent. Default to first if no id given
			List<Torrents> torrents = torrentGroup.getResponse().getTorrents();
			int idx, torrentId = getArguments().getInt(TorrentGroupActivity.TORRENT_ID, torrents.get(0).getId().intValue());
			//Zip down and find the torrent with the desired id
			for (idx = 0; idx < torrents.size() && torrents.get(idx).getId().intValue() != torrentId; ++idx) ;

			viewPager.setCurrentItem(idx);
		}
	}
}
