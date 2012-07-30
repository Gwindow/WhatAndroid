package what.top;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.torrents.torrents.DownloadDialog;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.cli.Utils;
import api.top.Response;
import api.util.Tuple;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jul 15, 2012 12:20:25 PM
 */
public class TopTenFragment extends SherlockFragment implements OnClickListener {
	private static final int DOWNLOAD_TAG = 0;
	private static final int GROUP_TAG = 1;
	private final List<Response> response;
	private final String tag;
	private LinearLayout scrollLayout;
	private MyScrollView scrollView;

	/**
	 * @param response
	 * @param snatchedTag
	 */
	public TopTenFragment(List<Response> response, String tag) {
		this.tag = tag;
		this.response = response;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populateMusic();
		return view;
	}

	private void populateMusic() {
		for (int j = 0; j < response.size(); j++) {
			if (response.get(j).getTag().equals(tag)) {
				for (int i = 0; i < response.get(j).getResults().size(); i++) {
					LinearLayout formats_torrent_layout =
							(LinearLayout) View.inflate(getSherlockActivity(), R.layout.formats_torrent, null);
					TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);

					String format_string = "";
					if (response.get(j).getResults().get(i).getArtist() != null
							&& response.get(j).getResults().get(i).getArtist().equalsIgnoreCase("false")) {
						format_string += response.get(j).getResults().get(i).getArtist() + " - ";
					}
					format_string += response.get(j).getResults().get(i).getGroupName();
					if (response.get(j).getResults().get(i).getGroupYear() != null
							&& response.get(j).getResults().get(i).getGroupYear().intValue() != 0) {
						format_string += " [" + response.get(j).getResults().get(i).getGroupYear() + "]";
					}

					format.setText(format_string);
					format.setOnClickListener(this);
					format.setId(GROUP_TAG);
					format.setTag(response.get(j).getResults().get(i).getGroupId().intValue());

					TextView size = (TextView) formats_torrent_layout.findViewById(R.id.size);
					size.setText("Data: " + Utils.toHumanReadableSize(response.get(j).getResults().get(i).getData().longValue()));
					TextView snatches = (TextView) formats_torrent_layout.findViewById(R.id.snatches);
					snatches.setText("Snatches: " + response.get(j).getResults().get(i).getSnatched());
					TextView seeders = (TextView) formats_torrent_layout.findViewById(R.id.seeders);
					seeders.setText("Seeders: " + response.get(j).getResults().get(i).getSeeders());
					TextView leechers = (TextView) formats_torrent_layout.findViewById(R.id.leechers);
					leechers.setText("Leechers: " + response.get(j).getResults().get(i).getLeechers());

					TextView download = (TextView) formats_torrent_layout.findViewById(R.id.download);
					download.setOnClickListener(this);
					download.setId(DOWNLOAD_TAG);
					download.setTag(new Tuple<Integer, String>(response.get(j).getResults().get(i).getTorrentId().intValue(),
							response.get(j).getResults().get(i).getDownloadLink()));
					scrollLayout.addView(formats_torrent_layout);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == DOWNLOAD_TAG) {
			@SuppressWarnings("unchecked")
			Tuple<Integer, String> tuple = ((Tuple<Integer, String>) v.getTag());
			new DownloadDialog(getSherlockActivity(), tuple.getA().intValue(), tuple.getB());
		}

		if (v.getId() == GROUP_TAG) {
			Intent intent = new Intent(getActivity(), TorrentGroupActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(BundleKeys.TORRENT_GROUP_ID, (Integer) v.getTag());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

}
