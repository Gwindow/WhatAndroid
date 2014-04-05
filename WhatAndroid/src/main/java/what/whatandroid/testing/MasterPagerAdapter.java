package what.whatandroid.testing;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter for the upper views (ie comments & torrent)
 */
public class MasterPagerAdapter extends FragmentPagerAdapter {
	private final String upper[] = { "Content 1", "Content 2", "Content 3"};
	private final String tabTitles[] = {"Tab 1", "Tab 2", "Tab 3"};

	public MasterPagerAdapter(FragmentManager fm){
		super(fm);
	}

	@Override
	public Fragment getItem(int position){
		return MasterContentFragment.newInstance(upper[position], position);
	}

	@Override
	public int getCount(){
		return upper.length;
	}

	@Override
	public CharSequence getPageTitle(int position){
		return tabTitles[position];
	}


}
