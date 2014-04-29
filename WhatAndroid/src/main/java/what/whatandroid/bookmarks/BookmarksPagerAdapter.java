package what.whatandroid.bookmarks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import api.soup.MySoup;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Pager adapter for swiping between the torrents and artists bookmarks fragments
 */
public class BookmarksPagerAdapter extends FragmentPagerAdapter implements OnLoggedInCallback {
	private TorrentBookmarksFragment torrentBookmarks;
	private ArtistBookmarksFragment artistBookmarks;

	public BookmarksPagerAdapter(FragmentManager fm){
		super(fm);
	}

	@Override
	public Fragment getItem(int position){
		switch (position){
			case 0:
				return new TorrentBookmarksFragment();
			default:
				return new ArtistBookmarksFragment();
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
				return "Torrents";
			default:
				return "Artists";
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		Fragment f = (Fragment)super.instantiateItem(container, position);
		if (position == 0){
			torrentBookmarks = (TorrentBookmarksFragment)f;
		}
		else {
			artistBookmarks = (ArtistBookmarksFragment)f;
		}
		if (MySoup.isLoggedIn()){
			((OnLoggedInCallback)f).onLoggedIn();
		}
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		switch (position){
			case 0:
				torrentBookmarks = null;
				break;
			default:
				artistBookmarks = null;
				break;
		}
	}

	@Override
	public void onLoggedIn(){
		if (torrentBookmarks != null){
			torrentBookmarks.onLoggedIn();
		}
		if (artistBookmarks != null){
			artistBookmarks.onLoggedIn();
		}
	}
}
