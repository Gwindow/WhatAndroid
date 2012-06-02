package what.torrents.artist;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.R;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.artist.TorrentGroup;

/**
 * @author Gwindow
 * @since Jun 2, 2012 10:35:15 AM
 */
public class MusicFragment extends Fragment implements OnClickListener {
	private static final int TORRENTGROUP_TAG = 0;
	private LinearLayout scrollLayout;
	private List<TorrentGroup> torrentGroups;

	public MusicFragment(List<TorrentGroup> torrentGroups) {
		this.torrentGroups = torrentGroups;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate(view, inflater);
		return view;
	}

	private void populate(View view, LayoutInflater inflater) {
		String release_type = "";
		for (int i = 0; i < torrentGroups.size(); i++) {
			if (!release_type.equals(torrentGroups.get(i).getReleaseType())) {
				release_type = torrentGroups.get(i).getReleaseType();
				TextView header = (TextView) inflater.inflate(R.layout.formats_header, null);
				scrollLayout.addView(header);
				String header_string = release_type;
				header.setText(header_string);
			}

			TextView torrentgroup_title = (TextView) inflater.inflate(R.layout.artist_torrentgroup_title, null);
			torrentgroup_title.setText(torrentGroups.get(i).getGroupYear() + " - " + torrentGroups.get(i).getGroupName());
			torrentgroup_title.setOnClickListener(this);
			torrentgroup_title.setTag(TORRENTGROUP_TAG);
			torrentgroup_title.setId(torrentGroups.get(i).getGroupId().intValue());
			scrollLayout.addView(torrentgroup_title);
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

}
