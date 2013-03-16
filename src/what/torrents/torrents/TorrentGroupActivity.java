package what.torrents.torrents;

import android.widget.Toast;
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
        System.out.println("On create options menu");
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        System.out.println("On prepare options menu");
        //Update the bookmark option text to reflect the change that will be executed
        MenuItem bookmarkItem = menu.findItem(R.id.torrent_group_bookmark);
        //Is there a way I can do this where neither will be null?
        //Will bookmarkItem ever be null? I don't think so. onPrepare calls after on create
        if (torrentGroup == null || bookmarkItem == null){
            System.out.println("on Prepare options "
                    + (torrentGroup == null ? "torrentGroup" : "bookmarkItem")
                    +" null");
        }
        else if (torrentGroup.getResponse().getGroup().isBookmarked())
            bookmarkItem.setTitle("Remove Bookmark");
        else
            bookmarkItem.setTitle("Bookmark");

        return super.onPrepareOptionsMenu(menu);
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
            case R.id.torrent_group_bookmark:
                new ChangeBookmark().execute();
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
			}
            else
				ErrorToast.show(TorrentGroupActivity.this, TorrentGroupActivity.class);

            System.out.println("On load finished");
		}
	}

	private class TorrentGroupAdapter extends FragmentPagerAdapter {
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
				fragment = ArtFragment.newInstance(torrentGroup.getResponse().getGroup().getWikiImage());
			}
			if (tag.equals(DESCRIPTION_TAB)) {
				fragment = DescriptionFragment.newInstance(torrentGroup.getResponse().getGroup().getWikiBody());
			}
			if (tag.equals(FORMATS_TAB)) {
				fragment = FormatsFragment.newInstance(torrentGroup.getResponse());
			}
			if (tag.equals(COMMENTS_TAB)) {
				fragment = CommentsFragment.newInstance(torrentGroupId, act);
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

    /**
     * Class for Async changing of the bookmark status of the TorrentGroup
     */
    private class ChangeBookmark extends AsyncTask<Void, Void, Boolean> implements Cancelable {
        public ChangeBookmark(){
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
            System.out.println("Trying to change bookmark status");
            Boolean isBookmarked = torrentGroup.getResponse().getGroup().isBookmarked();
            if (!isBookmarked){
                System.out.println("Adding bookmark");
                return torrentGroup.addBookmark();
            }
            else {
                System.out.println("Removing bookmark");
                return torrentGroup.removeBookmark();
            }
        }

        @Override
        protected void onPostExecute(Boolean status){
            //If it didn't go well, show error message
            if (!status){
                String err;
                if (torrentGroup.getResponse().getGroup().isBookmarked())
                    err = "Failed to remove bookmark";
                else
                    err = "Failed to add bookmark";

                Toast.makeText(TorrentGroupActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
