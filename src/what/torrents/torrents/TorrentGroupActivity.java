package what.torrents.torrents;

import what.fragments.ArtFragment;
import what.fragments.DescriptionFragment;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.R;
import what.torrents.artist.ArtistActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import api.torrents.torrents.TorrentGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since May 28, 2012 5:58:11 PM
 */
public class TorrentGroupActivity extends MyActivity2 {
	protected static final String MUSIC_CATEGORY = "Music";

	private static final String ART_TAB = "Art";
	private static final String DESCRIPTION_TAB = "Description";
	private static final String FORMATS_TAB = "Formats";
	private static final String COMMENTS_TAB = "Comments";

	// TODO add comments
	private static final String[] TABS = new String[] { ART_TAB, DESCRIPTION_TAB, FORMATS_TAB, COMMENTS_TAB };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private TorrentGroup torrentGroup;
	private int torrentGroupId;

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
			torrentGroupId = bundle.getInt(BundleKeys.TORRENT_GROUP_ID);
		} catch (Exception e) {
		}

		new Load().execute();

	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setContentView(R.layout.torrent_group_tabs);
		invalidateOptionsMenu();

		setActionBarTitle(torrentGroup.getResponse().getGroup().getName());

		adapter = new TorrentGroupAdapter(getSupportFragmentManager(), this);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (torrentGroup != null && torrentGroup.getResponse().getGroup().getCategoryName().equals(MUSIC_CATEGORY)) {
			MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.torrentgroup_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
		// TODO add bookmarks
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.artist:
				openArtist();
				break;
			case R.id.lastfm:
				openLastFM();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openArtist() {
		if (torrentGroup.getResponse().getGroup().getCategoryName().equals(MUSIC_CATEGORY)) {
			Intent intent = new Intent(this, ArtistActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(BundleKeys.ARTIST_ID, torrentGroup.getResponse().getGroup().getMusicInfo().getArtists().get(0).getId().intValue());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private void openLastFM() {
		Intent intent = new Intent();
		intent.setData(Uri.parse(torrentGroup.getLastFMUrl()));
		intent.setAction("android.intent.action.VIEW");
		startActivity(intent);
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
			torrentGroup = TorrentGroup.torrentGroupFromId(torrentGroupId);
			return torrentGroup.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(TorrentGroupActivity.this, TorrentGroupActivity.class);
		}
	}

	private class TorrentGroupAdapter extends FragmentPagerAdapter implements TitleProvider {
		MyActivity2 act;

		public TorrentGroupAdapter(FragmentManager fm, MyActivity2 act) {
			super(fm);
			this.act = act;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			if (tag.equals(ART_TAB)) {
				fragment = new ArtFragment(torrentGroup.getResponse().getGroup().getWikiImage());
			}
			if (tag.equals(DESCRIPTION_TAB)) {
				fragment = new DescriptionFragment(torrentGroup.getResponse().getGroup().getWikiBody());
			}
			if (tag.equals(FORMATS_TAB)) {
				fragment = new FormatsFragment(torrentGroup.getResponse());
			}
			if (tag.equals(COMMENTS_TAB)) {
				fragment = new CommentsFragment(torrentGroupId, act);
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
