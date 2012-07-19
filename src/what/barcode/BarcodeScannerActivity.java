package what.barcode;

import what.gui.ActivityNames;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import api.soup.MySoup;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jul 6, 2012 3:10:06 PM
 */
public class BarcodeScannerActivity extends MyActivity2 {
	private static final String SCAN_TAB = "Scan & Search";
	private static final String QUICK_TAB = "Quick Scan";
	// TODO add comments
	private static final String[] TABS_LOGGED_IN = new String[] { SCAN_TAB, QUICK_TAB };
	private static final String[] TABS_LOGGED_OUT = new String[] { QUICK_TAB };
	private static String[] tabs;

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.SCANNER);
		super.onCreate(savedInstanceState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		if (MySoup.isLoggedIn()) {
			tabs = TABS_LOGGED_IN;
		} else {
			tabs = TABS_LOGGED_OUT;
		}
		populate();
	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setContentView(R.layout.generic_pager);

		setActionBarTitle("Barcode Scanner");
		setActionBarTouchToHome(MySoup.isLoggedIn());

		adapter = new ScannerAdapater(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

	}

	private class ScannerAdapater extends FragmentPagerAdapter implements TitleProvider {
		public ScannerAdapater(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag;
			tag = tabs[position % tabs.length];

			if (tag.equals(SCAN_TAB)) {
				fragment = new ScannerFragment();
			}
			if (tag.equals(QUICK_TAB)) {
				fragment = new QuickScannerFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return tabs.length;
		}

		@Override
		public String getTitle(int position) {
			return tabs[position % tabs.length];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		// TODO Auto-generated method stub
	}
}