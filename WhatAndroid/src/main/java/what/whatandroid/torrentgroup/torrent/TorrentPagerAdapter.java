package what.whatandroid.torrentgroup.torrent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import api.torrents.torrents.TorrentGroup;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Adapter for swiping through the torrents in the group
 */
public class TorrentPagerAdapter extends FragmentStatePagerAdapter implements LoadingListener<TorrentGroup> {
	private SparseArray<TorrentDetailFragment> fragments;
	private TorrentGroup torrentGroup;

	public TorrentPagerAdapter(FragmentManager fm){
		super(fm);
		fragments = new SparseArray<TorrentDetailFragment>();
	}

	@Override
	public Fragment getItem(int position){
		if (torrentGroup != null){
			return TorrentDetailFragment.newInstance(torrentGroup.getResponse().getTorrents().get(position));
		}
		return new TorrentDetailFragment();
	}

	@Override
	public int getCount(){
		if (torrentGroup != null){
			return torrentGroup.getResponse().getTorrents().size();
		}
		return 1;
	}

	@Override
	public CharSequence getPageTitle(int position){
		if (torrentGroup != null){
			return torrentGroup.getResponse().getTorrents().get(position).getShortTitle();
		}
		return "Loading...";
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		TorrentDetailFragment f = (TorrentDetailFragment)super.instantiateItem(container, position);
		if (torrentGroup != null){
			f.onLoadingComplete(torrentGroup.getResponse().getTorrents().get(position));
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
	public void onLoadingComplete(TorrentGroup data){
		torrentGroup = data;
		for (int i = 0; i < fragments.size(); ++i){
			int pos = fragments.keyAt(i);
			fragments.get(pos).onLoadingComplete(torrentGroup.getResponse().getTorrents().get(pos));
		}
	}
}
