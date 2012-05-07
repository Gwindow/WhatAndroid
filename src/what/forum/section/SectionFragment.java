package what.forum.section;

import what.fragments.MyFragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Gwindow
 * @since May 5, 2012 5:57:16 PM
 */
public class SectionFragment extends MyFragmentPagerAdapter {

	/**
	 * @param content
	 * @param fm
	 */
	public SectionFragment(String[] content, FragmentManager fm) {
		super(content, fm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

}
