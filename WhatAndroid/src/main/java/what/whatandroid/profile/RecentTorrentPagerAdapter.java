package what.whatandroid.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import api.user.recent.RecentTorrent;
import what.whatandroid.callbacks.LoadingListener;

import java.util.List;

/**
 * Pager adapter for swiping through the recent torrents on the user profile page
 */
public class RecentTorrentPagerAdapter extends FragmentStatePagerAdapter implements LoadingListener<List<RecentTorrent>> {
	/**
	 * References to the visible fragments
	 */
	private SparseArray<RecentTorrentFragment> fragments;
	/**
	 * The list of torrents being displayed
	 */
	private List<RecentTorrent> torrents;

	public RecentTorrentPagerAdapter(FragmentManager fm){
		super(fm);
		fragments = new SparseArray<RecentTorrentFragment>();
	}

	@Override
	public Fragment getItem(int i){
		if (torrents != null){
			return RecentTorrentFragment.newInstance(torrents.get(i));
		}
		return RecentTorrentFragment.newInstance(null);
	}

	@Override
	public int getCount(){
		if (torrents != null){
			return torrents.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		RecentTorrentFragment f = (RecentTorrentFragment)super.instantiateItem(container, position);
		if (torrents != null){
			f.onLoadingComplete(torrents.get(position));
		}
		fragments.put(position, f);
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		fragments.remove(position);
	}

	@Override
	public void onLoadingComplete(List<RecentTorrent> data){
		torrents = data;
		for (int i = 0; i < fragments.size(); ++i){
			int pos = fragments.keyAt(i);
			fragments.get(pos).onLoadingComplete(torrents.get(pos));
		}
	}

	/**
	 * We want to show multiple recent torrents at once so have them each
	 * only take up 1/4 the view pager
	 */
	@Override
	public float getPageWidth(int position){
		return 0.3f;
	}
}
