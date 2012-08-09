package what.bookmarks;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.torrents.artist.ArtistActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.bookmarks.Artist;
import api.bookmarks.Bookmarks;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:40:42 PM
 */
public class ArtistsFragment extends SherlockFragment implements OnClickListener {
	private static final int ARTIST_TAG = 0;
	private LinearLayout scrollLayout;
	private Bookmarks bookmarks;
	private MyScrollView scrollView;

	public ArtistsFragment(Bookmarks bookmarks) {
		this.bookmarks = bookmarks;
	}

	public ArtistsFragment() {
		super();
	}

	public static SherlockFragment newInstance(Bookmarks bookmarks) {
		return new ArtistsFragment(bookmarks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		setHasOptionsMenu(true);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate();
		return view;
	}

	private void populate() {
		if (bookmarks != null) {
			Log.d("fragment", "populated");
			List<Artist> artists = bookmarks.getResponse().getArtists();
			Log.d("fragment", String.valueOf(artists.size()));
			for (int i = 0; i < artists.size(); i++) {
				TextView torrentgroup_title = (TextView) View.inflate(getSherlockActivity(), R.layout.bookmarks_torrentgroup_title, null);
				torrentgroup_title.setText(artists.get(i).getArtistName());
				torrentgroup_title.setOnClickListener(this);
				torrentgroup_title.setTag(ARTIST_TAG);
				torrentgroup_title.setId(artists.get(i).getArtistId().intValue());
				scrollLayout.addView(torrentgroup_title);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case ARTIST_TAG:
				openArtist(v.getId());
				break;
			default:
				break;
		}
	}

	private void openArtist(int id) {
		Intent intent = new Intent(getActivity(), ArtistActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.ARTIST_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

}