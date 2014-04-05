package what.whatandroid.testing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import what.whatandroid.R;

/**
 * Displays the top level content views
 */
public class MasterFragment extends Fragment {
	public interface ViewDetail {
		public void viewDetail(int position);
	}

	private MasterPagerAdapter pagerAdapter;
	private ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		pagerAdapter = new MasterPagerAdapter(getChildFragmentManager());
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);

		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);
		return view;
	}
}
