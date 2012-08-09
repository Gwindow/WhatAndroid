package what.bookmarks;

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
import api.bookmarks.Bookmarks;
import api.util.Tuple;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jun 6, 2012 2:52:40 PM
 */
public class BookmarksActivity extends MyActivity2 {
	private static final String COVERS_TAB = "Covers";
	private static final String TORRENTS_TAB = "Torrents";
	private static final String ARTISTS_TAB = "Artists";
	private static final String[] TABS = new String[] { TORRENTS_TAB, ARTISTS_TAB };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private Bookmarks artistBookmarks;
	private Bookmarks torrentBookmarks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if ((savedInstanceState != null)) {
			boolean refresh = savedInstanceState.getBoolean(BundleKeys.REFRESH);
			if (refresh) {
				refresh();
			}
		}

		super.setActivityName(ActivityNames.BOOKMARKS);
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
		setContentView(R.layout.bookmarks_tabs);

		setActionBarTitle("Bookmarks");

		adapter = new BookmarksAdapater(getSupportFragmentManager());

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
		protected Boolean doInBackground(Void... params) {
			Tuple<Boolean, Boolean> tuple = new Tuple<Boolean, Boolean>(false, false);
			boolean status = false;
			artistBookmarks = Bookmarks.initArtistBookmarks();
			tuple.setA(artistBookmarks.getStatus());

			torrentBookmarks = Bookmarks.initTorrentBookmarks();
			tuple.setB(torrentBookmarks.getStatus());
			if (tuple.getA() == true || tuple.getB() == true) {
				status = true;
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			BookmarksActivity.this.hideIndeterminateProgress();
			unlockScreenRotation();
			if (status) {
				populate();
			} else
				ErrorToast.show(BookmarksActivity.this, BookmarksActivity.class);
		}

	}

	public class BookmarksAdapater extends FragmentPagerAdapter implements TitleProvider {
		public BookmarksAdapater(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(COVERS_TAB)) {
				// fragment = new CoverArtGridFragment(torrentBookmarks);
			}
			if (tag.equals(TORRENTS_TAB)) {
				fragment = TorrentsFragment.newInstance(torrentBookmarks);
			}
			if (tag.equals(ARTISTS_TAB)) {
				fragment = ArtistsFragment.newInstance(artistBookmarks);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BundleKeys.REFRESH, true);
	}

}
