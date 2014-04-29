package what.whatandroid.bookmarks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment containing the view pager with the torrent and artist bookmarks listings
 */
public class BookmarksFragment extends Fragment implements OnLoggedInCallback {
	private BookmarksPagerAdapter bookmarksPagerAdapter;

	public BookmarksFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		bookmarksPagerAdapter = new BookmarksPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(bookmarksPagerAdapter);
		tabs.setViewPager(viewPager);
		return view;
	}

	@Override
	public void onLoggedIn(){
		bookmarksPagerAdapter.onLoggedIn();
	}
}
