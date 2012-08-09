package what.requests;

import what.fragments.ArtFragment;
import what.fragments.DescriptionFragment;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
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
import api.requests.Request;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jul 17, 2012 4:45:47 PM
 */
public class RequestActivity extends MyActivity2 {
	private static final String ART_TAB = "Art";
	private static final String DESC_TAB = "Description";
	private static final String DETAILS_TAB = "Details";

	private static final String[] TABS = new String[] { ART_TAB, DESC_TAB, DETAILS_TAB };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private Request request;
	private int requestId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if ((savedInstanceState != null)) {
			boolean refresh = savedInstanceState.getBoolean(BundleKeys.REFRESH);
			if (refresh) {
				refresh();
			}
		}
		super.setActivityName(ActivityNames.REQUEST);
		super.requestIndeterminateProgress();
		super.onCreate(savedInstanceState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		try {
			requestId = bundle.getInt(BundleKeys.REQUEST_ID);
		} catch (Exception e) {
		}

		new Load().execute();

	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		invalidateOptionsMenu();
		setContentView(R.layout.generic_pager);

		setActionBarTitle(request.getResponse().getTitle());

		adapter = new Adapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

	}

	@Override
	public void onPause() {
		try {
			ArtFragment.recyle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
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
			request = Request.requestFromId(requestId);
			return request.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(RequestActivity.this, RequestActivity.class);
		}
	}

	private class Adapter extends FragmentPagerAdapter implements TitleProvider {
		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(ART_TAB)) {
				fragment = new ArtFragment(request.getResponse().getImage());
			}
			if (tag.equals(DESC_TAB)) {
				fragment = new DescriptionFragment(request.getResponse().getDescription());
			}
			if (tag.equals(DETAILS_TAB)) {
				fragment = new DetailsFragment(request.getResponse());
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BundleKeys.REFRESH, true);
	}

}
