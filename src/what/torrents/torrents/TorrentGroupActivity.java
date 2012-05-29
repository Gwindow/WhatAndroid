package what.torrents.torrents;

import what.gui.ActivityNames;
import what.gui.BundleKeys;
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
import api.torrents.torrents.TorrentGroup;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * @author Gwindow
 * @since May 28, 2012 5:58:11 PM
 */
public class TorrentGroupActivity extends MyActivity2 {
	private static final String[] TABS = new String[] { "Album Art", "Description", "Formats", "Comments" };

	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;

	private TorrentGroup torrentGroup;
	private int torrentGroupId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.MUSIC);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrent_group_tabs);

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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		new Load().execute();

	}

	// TODO make this less sloppy. Create a custom fragment activity.
	private void populate() {
		setActionBarTitle(torrentGroup.getResponse().getGroup().getName());

		adapter = new TorrentGroupAdapter(getSupportFragmentManager());

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
			dialog = new ProgressDialog(TorrentGroupActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			torrentGroup = TorrentGroup.torrentGroupFromId(torrentGroupId);
			return torrentGroup.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			unlockScreenRotation();

			if (status) {
				populate();
			} else
				ErrorToast.show(TorrentGroupActivity.this, TorrentGroupActivity.class);
		}
	}

	private class TorrentGroupAdapter extends FragmentPagerAdapter implements TitleProvider {
		public TorrentGroupAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TorrentGroupFragment.newInstance(TABS[position % TABS.length]);
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

}
