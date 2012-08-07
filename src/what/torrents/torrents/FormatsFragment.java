package what.torrents.torrents;

import java.util.List;

import what.gui.DownloadDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.cli.Utils;
import api.torrents.torrents.Response;
import api.torrents.torrents.Torrents;
import api.util.Tuple;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 1, 2012 10:58:17 PM
 */
public class FormatsFragment extends SherlockFragment implements OnClickListener, OnLongClickListener {
	private static final int DOWNLOAD_TAG = 0;

	private LinearLayout scrollLayout;
	private Response response;

	private MyScrollView scrollView;

	public FormatsFragment(Response response) {
		this.response = response;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		if (response.getGroup().getCategoryName().equals(TorrentGroupActivity.MUSIC_CATEGORY)) {
			populateMusic(view, inflater);
		} else {
			populateOther(view, inflater);
		}

		return view;
	}

	private void populateOther(View view, LayoutInflater inflater) {
		List<Torrents> torrents = response.getTorrents();
		for (int i = 0; i < torrents.size(); i++) {
			LinearLayout formats_torrent_layout = (LinearLayout) inflater.inflate(R.layout.formats_torrent, null);

			TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);
			String format_string = "";
			if (torrents.get(i).isFreeTorrent() == true) {
				format_string += "FreeLeech! ";
			}
			if (response.getGroup().getYear() != null && response.getGroup().getYear().intValue() != 0) {
				format_string += "[" + response.getGroup().getYear() + "] ";
			}
			format_string += response.getGroup().getName();
			format.setText(format_string);

			TextView size = (TextView) formats_torrent_layout.findViewById(R.id.size);
			size.setText("Size: " + Utils.toHumanReadableSize(torrents.get(i).getSize().longValue()));
			TextView snatches = (TextView) formats_torrent_layout.findViewById(R.id.snatches);
			snatches.setText("Snatches: " + torrents.get(i).getSnatched());
			TextView seeders = (TextView) formats_torrent_layout.findViewById(R.id.seeders);
			seeders.setText("Seeders: " + torrents.get(i).getSeeders());
			TextView leechers = (TextView) formats_torrent_layout.findViewById(R.id.leechers);
			leechers.setText("Leechers: " + torrents.get(i).getLeechers());

			TextView download = (TextView) formats_torrent_layout.findViewById(R.id.download);
			download.setOnClickListener(this);
			download.setId(DOWNLOAD_TAG);
			download.setTag(new Tuple<Integer, String>(torrents.get(i).getId().intValue(), torrents.get(i).getDownloadLink()));
			scrollLayout.addView(formats_torrent_layout);
		}

	}

	private void populateMusic(View view, LayoutInflater inflater) {
		// TODO fix empty strings
		// TODO log scores
		List<Torrents> torrents = response.getTorrents();
		String remaster = "";
		for (int i = 0; i < torrents.size(); i++) {
			if (!remaster.equals(torrents.get(i).getRemaster())) {
				remaster = torrents.get(i).getRemaster();
				TextView header = (TextView) inflater.inflate(R.layout.formats_header, null);
				scrollLayout.addView(header);
				String header_string =
						torrents.get(i).isRemastered() == true ? torrents.get(i).getRemaster() : response.getGroup().getOriginal();
				header.setText(header_string);
			}
			LinearLayout formats_torrent_layout = (LinearLayout) inflater.inflate(R.layout.formats_torrent, null);

			TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);
			String format_string =
					torrents.get(i).isFreeTorrent() == true ? "Freeleech! " + torrents.get(i).getMediaFormatEncoding() : torrents.get(i)
							.getMediaFormatEncoding();
			format.setText(format_string);
			format.setOnLongClickListener(this);

			Object[] array = new Object[6];
			array[0] = torrents.get(i).getId();
			array[1] = torrents.get(i).getDownloadLink();
			array[2] = torrents.get(i).getSize();
			array[3] = torrents.get(i).getSnatched();
			array[4] = torrents.get(i).getSeeders();
			array[5] = torrents.get(i).getLeechers();
			format.setTag(array);

			scrollLayout.addView(formats_torrent_layout);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onLongClick(View v) {
		Object[] array = (Object[]) v.getTag();
		new DownloadDialog(getSherlockActivity(), (Number) array[0], (String) array[1], (Number) array[2], (Number) array[3],
				(Number) array[4], (Number) array[5]);
		return false;
	}

	@Override
	public void onClick(View v) {

	}
}