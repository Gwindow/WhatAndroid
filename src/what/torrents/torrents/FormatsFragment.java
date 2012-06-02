package what.torrents.torrents;

import java.util.List;

import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.torrents.torrents.Response;
import api.torrents.torrents.Torrents;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 1, 2012 10:58:17 PM
 */
public class FormatsFragment extends SherlockFragment {
	private LinearLayout scrollLayout;
	private Response response;

	public FormatsFragment(Response response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);

		// TODO take care of other categories
		if (response.getGroup().getCategoryName().equals(TorrentGroupActivity.MUSIC_CATEGORY)) {
			populateMusic(view, inflater);
		}

		return view;
	}

	private void populateMusic(View view, LayoutInflater inflater) {
		// TODO fix empty strings
		List<Torrents> torrents = response.getTorrents();
		String remaster = "";
		for (int i = 0; i < torrents.size(); i++) {
			if (!remaster.equals(torrents.get(i).getRemaster())) {
				remaster = torrents.get(i).getRemaster();
				TextView header = (TextView) inflater.inflate(R.layout.formats_header, null);
				scrollLayout.addView(header);
				String header_string =
						torrents.get(i).isRemastered() == true ? torrents.get(i).getRemaster() : response.getGroup()
								.getOriginal();
				header.setText(header_string);
			}
			LinearLayout formats_torrent_layout = (LinearLayout) inflater.inflate(R.layout.formats_torrent, null);
			TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);
			String format_string =
					torrents.get(i).isFreeTorrent() == true ? "Freeleech! " + torrents.get(i).getMediaFormatEncoding() : torrents
							.get(i).getMediaFormatEncoding();
			format.setText(format_string);
			scrollLayout.addView(formats_torrent_layout);
		}
	}
}