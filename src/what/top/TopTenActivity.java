package what.top;

import what.gui.ActivityNames;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import api.top.Top;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jul 8, 2012 12:37:14 AM
 */
public class TopTenActivity extends MyActivity2 {
	private static final int LIMIT = 10;
	private static final String UPLOADED_PAST_DAY_TAB = "Uploaded Past Day";
	private static final String UPLOADED_PAST_WEEK_TAB = "Uploaded Past Week";
	private static final String ACTIVE_ALL_TIME_TAB = "Most Active";
	private static final String SNATCHED_ALL_TIME_TAB = "Most Snatched";
	private static final String TRANS_ALL_TIME_TAB = "Most Transferred";
	private static final String SEEDED_ALL_TIME_TAB = "Best Seeded";
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

		adapter = new Adapater(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(TopTenActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			top = Top.initTopTorrents(LIMIT);
			return top.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(TopTenActivity.this, TopTenActivity.class);
		}
	}

	private class Adapater extends FragmentPagerAdapter implements TitleProvider {
		public Adapater(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(UPLOADED_PAST_DAY_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), DAY_TAG);
			}
			if (tag.equals(UPLOADED_PAST_WEEK_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), WEEK_TAG);
			}
			if (tag.equals(ACTIVE_ALL_TIME_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), OVERALL_TAG);
			}
			if (tag.equals(SNATCHED_ALL_TIME_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), SNATCHED_TAG);
			}
			if (tag.equals(TRANS_ALL_TIME_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), DATA_TAG);
			}
			if (tag.equals(SEEDED_ALL_TIME_TAB)) {
				fragment = new TopTenFragment(top.getResponse(), SEEDED_TAG);
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

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}
}