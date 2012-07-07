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
	private static final String[] TABS = new String[] { SCAN_TAB, QUICK_TAB };

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
		populate();
	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setContentView(R.layout.generic_pager);

		setActionBarTitle("Barcode Scanner");

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
			String tag = TABS[position % TABS.length];
			if (MySoup.isLoggedIn()) {
				if (tag.equals(SCAN_TAB)) {
					// fragment = new ScannerFragment();
				}
			}
			if (tag.equals(QUICK_TAB)) {
				// fragment = new QuickScannerFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return TABS.length;
		}

		@Override
		public String getTitle(int position) {
			return TABS[position % TABS.length];
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