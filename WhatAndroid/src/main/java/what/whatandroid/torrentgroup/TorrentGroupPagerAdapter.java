package what.whatandroid.torrentgroup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import api.torrents.torrents.TorrentGroup;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Pager adapter for swiping between the torrent group overview and comments
 */
public class TorrentGroupPagerAdapter extends FragmentPagerAdapter implements LoadingListener<TorrentGroup> {
	/**
	 * The torrent group overview and comments fragments
	 */
	private TorrentGroupOverviewFragment overview;
	private TorrentCommentsFragment comments;
	private TorrentGroup torrentGroup;
	private int groupId;

	public TorrentGroupPagerAdapter(FragmentManager fm, int groupId){
		super(fm);
		this.groupId = groupId;
	}

	@Override
	public Fragment getItem(int position){
		switch (position){
			case 0:
				return new TorrentGroupOverviewFragment();
			default:
				return TorrentCommentsFragment.newInstance(groupId);
		}
	}

	@Override
	public int getCount(){
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position){
		switch (position){
			case 0:
				return "Torrent Group";
			default:
				return "Comments";
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		if (position == 0){
			overview = (TorrentGroupOverviewFragment)super.instantiateItem(container, position);
			if (torrentGroup != null){
				overview.onLoadingComplete(torrentGroup);
			}
			return overview;
		}
		else {
			comments = (TorrentCommentsFragment)super.instantiateItem(container, position);
			if (torrentGroup != null){
				//We call logged in on the fragment if loading's done to let it know that it can start loading
				comments.onLoadingComplete(torrentGroup);
			}
			return comments;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		if (position == 0){
			overview = null;
		}
		else {
			comments = null;
		}
	}

	@Override
	public void onLoadingComplete(TorrentGroup group){
		torrentGroup = group;
		if (overview != null){
			overview.onLoadingComplete(torrentGroup);
		}
		if (comments != null){
			comments.onLoadingComplete(torrentGroup);
		}
	}
}
