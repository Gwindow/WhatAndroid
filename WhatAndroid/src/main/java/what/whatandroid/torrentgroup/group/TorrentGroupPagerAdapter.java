package what.whatandroid.torrentgroup.group;

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
	private DescriptionFragment description;
	private TorrentGroup torrentGroup;
	private int groupId;

	public TorrentGroupPagerAdapter(FragmentManager fm, int groupId) {
		super(fm);
		this.groupId = groupId;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new TorrentGroupOverviewFragment();
			case 1:
				return new DescriptionFragment();
			default:
				return TorrentCommentsFragment.newInstance(groupId);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Torrent Group";
			case 1:
				return "Description";
			default:
				return "Comments";
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment f = (Fragment) super.instantiateItem(container, position);
		if (position == 0) {
			overview = (TorrentGroupOverviewFragment) f;
			if (torrentGroup != null) {
				overview.onLoadingComplete(torrentGroup);
			}
		} else if (position == 1) {
			description = (DescriptionFragment) f;
			if (torrentGroup != null) {
				description.onLoadingComplete(torrentGroup.getResponse().getGroup().getWikiBody());
			}
		} else {
			comments = (TorrentCommentsFragment) f;
			if (torrentGroup != null) {
				//We call logged in on the fragment if loading's done to let it know that it can start loading
				comments.onLoadingComplete(torrentGroup);
			}
		}
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		switch (position) {
			case 0:
				overview = null;
				break;
			case 1:
				description = null;
				break;
			default:
				comments = null;
		}
	}

	@Override
	public void onLoadingComplete(TorrentGroup group) {
		torrentGroup = group;
		if (overview != null) {
			overview.onLoadingComplete(torrentGroup);
		}
		if (description != null) {
			description.onLoadingComplete(torrentGroup.getResponse().getGroup().getWikiBody());
		}
		if (comments != null) {
			comments.onLoadingComplete(torrentGroup);
		}
	}
}
