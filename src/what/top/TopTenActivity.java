package what.top;

import what.gui.ActivityNames;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import api.top.Top;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

/**
 * @author Gwindow
 * @since Jul 8, 2012 12:37:14 AM
 */
public class TopTenActivity extends MyActivity2 {
	private static final int LIMIT = 10;
	private static final String UPLOADED_PAST_DAY_TAB = "Day";
	private static final String UPLOADED_PAST_WEEK_TAB = "Week";
	private static final String ACTIVE_ALL_TIME_TAB = "Active";
	private static final String SNATCHED_ALL_TIME_TAB = "Snatched";
	private static final String TRANS_ALL_TIME_TAB = "Transferred";
	private static final String SEEDED_ALL_TIME_TAB = "Seeded";
	private static final String[] TABS = new String[] { UPLOADED_PAST_DAY_TAB, UPLOADED_PAST_WEEK_TAB, ACTIVE_ALL_TIME_TAB,
			SNATCHED_ALL_TIME_TAB, TRANS_ALL_TIME_TAB, SEEDED_ALL_TIME_TAB };

	protected static final String DAY_TAG = "day";
	protected static final String WEEK_TAG = "week";
	protected static final String OVERALL_TAG = "overall";
	protected static final String SNATCHED_TAG = "snatched";
	protected static final String DATA_TAG = "data";
	protected static final String SEEDED_TAG = "seeded";

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private Top top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.TOPTEN);
		super.requestIndeterminateProgress();
		super.onCreate(savedInstanceState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		new Load().execute();
	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setContentView(R.layout.generic_pager);

		setActionBarTitle("Top 10");

		adapter = new Adapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		public Load() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			top = Top.initTopTorrents(LIMIT);
			return top.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(TopTenActivity.this, TopTenActivity.class);
		}
	}

	public class Adapter extends FragmentPagerAdapter {
		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(UPLOADED_PAST_DAY_TAB)) {
				fragment = TopTenFragment.newInstance(top, DAY_TAG);
			}
			if (tag.equals(UPLOADED_PAST_WEEK_TAB)) {
				fragment = TopTenFragment.newInstance(top, WEEK_TAG);
			}
			if (tag.equals(ACTIVE_ALL_TIME_TAB)) {
				fragment = TopTenFragment.newInstance(top, OVERALL_TAG);
			}
			if (tag.equals(SNATCHED_ALL_TIME_TAB)) {
				fragment = TopTenFragment.newInstance(top, SNATCHED_TAG);
			}
			if (tag.equals(TRANS_ALL_TIME_TAB)) {
				fragment = TopTenFragment.newInstance(top, DATA_TAG);
			}
			if (tag.equals(SEEDED_ALL_TIME_TAB)) {
				fragment = TopTenFragment.newInstance(top, SEEDED_TAG);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return TABS.length;
		}

        @Override
        public String getPageTitle(int position) {
			return TABS[position % TABS.length];
		}
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}
}