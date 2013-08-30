package what.torrents.artist;

import android.widget.Toast;
import api.soup.MySoup;
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
    public boolean onPrepareOptionsMenu(Menu menu){
        //Set the bookmark item text to match the current state
        MenuItem bookmarkItem = menu.findItem(R.id.artist_bookmark);
        if (artist == null || bookmarkItem == null){
            System.out.println("on Prepare options "
                    + (artist == null ? "artist" : "bookmarkItem")
                    +" null");
        }
        else if (artist.getResponse().isBookmarked())
            bookmarkItem.setTitle("Remove Bookmark");
        else
            bookmarkItem.setTitle("Bookmark");

        //Set the notifications item text to match the current state
        MenuItem notificationsItem = menu.findItem(R.id.artist_notifications);
        if (artist == null || notificationsItem == null){
            System.out.println("on Prepare options "
                    + (artist == null ? "artist" : "notificationsItem")
                    +" null");
        }
        else if (!MySoup.canNotifications())
            notificationsItem.setVisible(false);
        else if (artist.getResponse().hasNotificationsEnabled())
            notificationsItem.setTitle("Do not notify of new uploads");
        else
            notificationsItem.setTitle("Notify of new uploads");

        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.lastfm:
				openLastFM();
				break;
            case R.id.artist_bookmark:
                new ChangeBookmark().execute();
                break;
            case R.id.artist_notifications:
                new ChangeNotifications().execute();
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
			artist = Artist.fromId(artistId);
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

	public class ArtistAdapater extends FragmentPagerAdapter {
		public ArtistAdapater(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			String tag = TABS[position % TABS.length];
			//TODO: Does this really imply that we're creating new fragments each time
			//we swipe to a new one?
			if (tag.equals(ART_TAB)) {
				fragment = ArtFragment.newInstance(artist.getResponse().getImage());
			}
			if (tag.equals(DESCRIPTION_TAB)) {
				fragment = DescriptionFragment.newInstance(artist.getResponse().getBody());
			}
			if (tag.equals(MUSIC_TAB)) {
				fragment = MusicFragment.newInstance(artist.getResponse().getTorrentgroup());
			}
			if (tag.equals(REQUESTS_TAB)) {
				fragment = RequestFragment.newInstance(artist.getResponse().getRequests());
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
     * Class for Async changing of the bookmark status of the Artist
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
            Boolean isBookmarked = artist.getResponse().isBookmarked();
            if (!isBookmarked){
                return artist.addBookmark();
            }
            else {
                return artist.removeBookmark();
            }
        }

        @Override
        protected void onPostExecute(Boolean status){
            //Display some status information, note that we use !bookmarked if we suceeded
            //since the status we know have has changed
            String info;
            if (status)
                info = (!artist.getResponse().isBookmarked() ? "Removed" : "Added")
                        + " artist bookmark";
            else
                info = "Failed to " + (artist.getResponse().isBookmarked() ? "remove" : "add")
                        + " artist bookmark";

            Toast.makeText(ArtistActivity.this, info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Class for Async changing of the notification enabled status of the Artist
     */
    private class ChangeNotifications extends AsyncTask<Void, Void, Boolean> implements Cancelable {
        public ChangeNotifications(){
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
            Boolean notificationsEnabled = artist.getResponse().hasNotificationsEnabled();
            if (!notificationsEnabled)
                return artist.enableNotifications();
            else
                return artist.disableNotifications();
        }

        @Override
        protected void onPostExecute(Boolean status){
            //Display some status information, note that we use !enabled if we succeeded since
            //if we were successful the status has changed
            String info;
            if (status)
                info = (!artist.getResponse().hasNotificationsEnabled() ? "Disabled" : "Enabled")
                        + " notifications";
            else
                info = "Failed to " + (artist.getResponse().hasNotificationsEnabled() ? "disable" : "enable")
                        + " notifications";

            Toast.makeText(ArtistActivity.this, info, Toast.LENGTH_SHORT).show();
        }
    }
}