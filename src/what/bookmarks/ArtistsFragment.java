package what.bookmarks;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.R;
import what.torrents.artist.ArtistActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.bookmarks.Artist;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:40:42 PM
 */
public class ArtistsFragment extends SherlockFragment implements OnClickListener {
	private static final int ARTIST_TAG = 0;
	private LinearLayout scrollLayout;
	private List<Artist> bookmarks;

	public ArtistsFragment(List<Artist> bookmarks) {
		this.bookmarks = bookmarks;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate(view, inflater);
		return view;
	}

	private void populate(View view, LayoutInflater inflater) {
		for (int i = 0; i < bookmarks.size(); i++) {
			TextView torrentgroup_title = (TextView) inflater.inflate(R.layout.bookmarks_torrentgroup_title, null);
			torrentgroup_title.setText(bookmarks.get(i).getArtistName());
			torrentgroup_title.setOnClickListener(this);
			torrentgroup_title.setTag(ARTIST_TAG);
			torrentgroup_title.setId(bookmarks.get(i).getArtistId().intValue());
			scrollLayout.addView(torrentgroup_title);
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
}