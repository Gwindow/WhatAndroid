package what.bookmarks;

import java.util.List;

import android.util.TypedValue;
import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.bookmarks.Bookmarks;
import api.bookmarks.TorrentGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:41:50 PM
 */
public class TorrentsFragment extends SherlockFragment implements OnClickListener {
	private static final int TORRENTGROUP_TAG = 0;
	private LinearLayout scrollLayout;
	private Bookmarks bookmarks;
	private MyScrollView scrollView;

	public TorrentsFragment(Bookmarks bookmarks) {
		this.bookmarks = bookmarks;
	}

	public TorrentsFragment() {
		super();
	}

	public static SherlockFragment newInstance(Bookmarks bookmarks) {
		return new TorrentsFragment(bookmarks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		setHasOptionsMenu(true);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate(view, inflater);
		return view;
	}

	private void populate(View view, LayoutInflater inflater) {
		if (bookmarks != null) {
			List<TorrentGroup> torrents = bookmarks.getResponse().getTorrents();
			for (int i = 0; i < torrents.size(); i++) {
				TextView torrentgroup_title = (TextView) inflater.inflate(R.layout.bookmarks_torrentgroup_title, null);
                //TODO: How to get the artist name when it's not part of the response? I don't really want to do
                //TODO: mulitiple api requests for every bookmarked torrent.
				torrentgroup_title.setText(torrents.get(i).getName());
				torrentgroup_title.setOnClickListener(this);
				torrentgroup_title.setTag(TORRENTGROUP_TAG);
				torrentgroup_title.setId(torrents.get(i).getId().intValue());
				scrollLayout.addView(torrentgroup_title);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case TORRENTGROUP_TAG:
				openTorrentGroup(v.getId());
				break;
			default:
				break;
		}
	}

	private void openTorrentGroup(int id) {
		Intent intent = new Intent(getActivity(), TorrentGroupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.TORRENT_GROUP_ID, id);
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
