package what.whatandroid.torrentgroup;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.torrents.torrents.TorrentGroup;
import com.astuetz.PagerSlidingTabStrip;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Fragment for showing swipeable views of the torrent group overview and comments
 */
public class TorrentGroupFragment extends android.support.v4.app.Fragment implements LoadingListener<TorrentGroup> {
	private TorrentGroupPagerAdapter torrentGroupPagerAdapter;
	private ViewPager viewPager;
	private PagerSlidingTabStrip tabs;

	public static TorrentGroupFragment newInstance(int groupId){
		TorrentGroupFragment f = new TorrentGroupFragment();
		Bundle args = new Bundle();
		args.putInt(TorrentGroupActivity.GROUP_ID, groupId);
		f.setArguments(args);
		return f;
	}

	public TorrentGroupFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		int groupId;
		if (savedInstanceState != null){
			groupId = savedInstanceState.getInt(TorrentGroupActivity.GROUP_ID);
		}
		else {
			groupId = getArguments().getInt(TorrentGroupActivity.GROUP_ID);
		}

		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		torrentGroupPagerAdapter = new TorrentGroupPagerAdapter(getChildFragmentManager(), groupId);
		viewPager.setAdapter(torrentGroupPagerAdapter);
		tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);
		return view;
	}

	@Override
	public void onLoadingComplete(TorrentGroup group){
		torrentGroupPagerAdapter.onLoadingComplete(group);
	}
}
