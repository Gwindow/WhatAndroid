package what.torrents.torrents;

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
import what.gui.DownloadDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;

import java.util.List;

/**
 * @author Gwindow
 * @since Jun 1, 2012 10:58:17 PM
 */
public class FormatsFragment extends SherlockFragment implements OnClickListener, OnLongClickListener {
	private static final int DOWNLOAD_TAG = 0;

	private LinearLayout scrollLayout;
	private Response response;

	private MyScrollView scrollView;

	public FormatsFragment() {
		super();
	}

	public FormatsFragment(Response response) {
		this.response = response;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: Shouldn't we also call super.onCreateView here?
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
        for (Torrents t : torrents){
            LinearLayout formats_torrent_layout = (LinearLayout) inflater.inflate(R.layout.formats_torrent, null);

            TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);
            String format_string = "";
            if (t.isFreeTorrent())
                format_string += "FreeLeech! ";
            if (response.getGroup().getYear() != null && response.getGroup().getYear().intValue() != 0)
                format_string += "[" + response.getGroup().getYear() + "] ";

            format_string += response.getGroup().getName();
            format.setText(format_string);

            TextView size = (TextView) formats_torrent_layout.findViewById(R.id.size);
            size.setText("Size: " + Utils.toHumanReadableSize(t.getSize().longValue()));
            TextView snatches = (TextView) formats_torrent_layout.findViewById(R.id.snatches);
            snatches.setText("Snatches: " + t.getSnatched());
            TextView seeders = (TextView) formats_torrent_layout.findViewById(R.id.seeders);
            seeders.setText("Seeders: " + t.getSeeders());
            TextView leechers = (TextView) formats_torrent_layout.findViewById(R.id.leechers);
            leechers.setText("Leechers: " + t.getLeechers());

            TextView download = (TextView) formats_torrent_layout.findViewById(R.id.download);
            download.setOnClickListener(this);
            download.setId(DOWNLOAD_TAG);
            download.setTag(new Tuple<Integer, String>(t.getId().intValue(), t.getDownloadLink()));
            scrollLayout.addView(formats_torrent_layout);
        }
	}

	private void populateMusic(View view, LayoutInflater inflater) {
		// TODO fix empty strings
		// TODO log scores
		List<Torrents> torrents = response.getTorrents();
		String remaster = "";
        for (Torrents t : torrents){
            if (!remaster.equals(t.getRemaster())) {
                remaster = t.getRemaster();
                TextView header = (TextView) inflater.inflate(R.layout.formats_header, null);
                scrollLayout.addView(header);
                String header_string =
                        t.isRemastered() ? t.getRemaster() : response.getGroup().getOriginal();
                header.setText(header_string);
            }
            LinearLayout formats_torrent_layout = (LinearLayout) inflater.inflate(R.layout.formats_torrent, null);

            TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);
            String format_string =
                    t.isFreeTorrent() ? "Freeleech! " + t.getMediaFormatEncoding() : t.getMediaFormatEncoding();
            format.setText(format_string);
            format.setOnLongClickListener(this);

            Object[] array = new Object[7];
            array[0] = t.getId();
            array[1] = t.getDownloadLink();
            array[2] = t.getSize();
            array[3] = t.getSnatched();
            array[4] = t.getSeeders();
            array[5] = t.getLeechers();
            array[6] = response.getGroup().getName();
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
				(Number) array[4], (Number) array[5], (String)array[6]);
		return false;
	}

	@Override
	public void onClick(View v) {

	}

	/**
     * TODO: What uses this?
	 * @param
	 * @return
	 */
	public static SherlockFragment newInstance(Response response) {
		return new FormatsFragment(response);
	}
}