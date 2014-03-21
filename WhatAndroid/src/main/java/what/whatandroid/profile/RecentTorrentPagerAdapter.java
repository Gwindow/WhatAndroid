package what.whatandroid.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import api.user.recent.RecentTorrent;

import java.util.List;

/**
 * Pager adapter for swiping through the recent torrents on the user profile page
 */
public class RecentTorrentPagerAdapter extends FragmentStatePagerAdapter {
	/**
	 * The list of torrents being displayed
	 */
	private final List<RecentTorrent> torrents;

	public RecentTorrentPagerAdapter(List<RecentTorrent> tor, FragmentManager fm){
		super(fm);
		torrents = tor;
	}

	@Override
	public Fragment getItem(int i){
		return RecentTorrentFragment.newInstance(torrents.get(i));
	}

	@Override
	public int getCount(){
		return torrents.size();
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
