package what.torrents.artist;

import what.fragments.ArtFragment;
import what.fragments.DescriptionFragment;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import api.torrents.artist.Artist;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since Jun 2, 2012 10:24:17 AM
 */
public class ArtistActivity extends MyActivity2 {
	private static final String ART_TAB = "Art";
	private static final String DESCRIPTION_TAB = "Description";
	private static final String MUSIC_TAB = "Music";
	private static final String REQUESTS_TAB = "Requests";
	// TODO add comments
	private static final String[] TABS = new String[] { ART_TAB, DESCRIPTION_TAB, MUSIC_TAB, REQUESTS_TAB };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private Artist artist;
	private int artistId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if ((savedInstanceState != null)) {
			boolean refresh = savedInstanceState.getBoolean(BundleKeys.REFRESH);
			if (refresh) {
				refresh();
			}
		}

		super.setActivityName(ActivityNames.MUSIC);
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
			artistId = bundle.getInt(BundleKeys.ARTIST_ID);
		} catch (Exception e) {
		}

		new Load().execute();

	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setContentView(R.layout.artist_tabs);

		setActionBarTitle(artist.getResponse().getName());

		adapter = new ArtistAdapater(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.artist_menu, menu);
		// TODO add bookmarks
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.lastfm:
				openLastFM();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openLastFM() {
		Intent intent = new Intent();
		intent.setData(Uri.parse(artist.getLastFMUrl()));
		intent.setAction("android.intent.action.VIEW");
		startActivity(intent);
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
			artist = Artist.artistFromId(artistId);
			return artist.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(ArtistActivity.this, ArtistActivity.class);
		}
	}

	private class ArtistAdapater extends FragmentPagerAdapter implements TitleProvider {
		public ArtistAdapater(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(ART_TAB)) {
				fragment = new ArtFragment(artist.getResponse().getImage());
			}
			if (tag.equals(DESCRIPTION_TAB)) {
				fragment = new DescriptionFragment(artist.getResponse().getBody());
			}
			if (tag.equals(MUSIC_TAB)) {
				fragment = new MusicFragment(artist.getResponse().getTorrentgroup());
			}
			if (tag.equals(REQUESTS_TAB)) {
				fragment = new RequestFragment(artist.getResponse().getRequests());
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
	public void onPause() {
		try {
			ArtFragment.recyle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
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