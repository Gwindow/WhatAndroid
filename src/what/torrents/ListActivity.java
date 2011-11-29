package what.torrents;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import what.gui.ReportSender;
import what.torrents.artist.ArtistTabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListActivity extends MyActivity implements OnLongClickListener {
	private LinearLayout scrollLayout;
	private ArrayList<TextView> textViewList = new ArrayList<TextView>();
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ReportSender sender = new ReportSender(this);
		getBundle();
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		populateLayout();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		type = b.getString("type");
	}

	private void populateLayout() {
		if (type.equals("artist_tags")) {
			if (ArtistTabActivity.getArtist().getStatus()) {
				for (int i = 0; i < ArtistTabActivity.getArtist().getResponse().getTags().size(); i++) {
					textViewList.add(new TextView(this));
					textViewList.get(i).setText(ArtistTabActivity.getArtist().getResponse().getTags().get(i).getName());
					textViewList.get(i).setTextSize(17);
					textViewList.get(i).setId(i);
					textViewList.get(i).setOnLongClickListener(this);
					scrollLayout.addView(textViewList.get(i));
				}
			}

		}

		if (type.equals("artist_similiar")) {
			if (ArtistTabActivity.getArtist().getStatus()) {
				for (int i = 0; i < ArtistTabActivity.getArtist().getResponse().getSimilarArtists().size(); i++) {
					textViewList.add(new TextView(this));
					textViewList.get(i).setText(ArtistTabActivity.getArtist().getResponse().getSimilarArtists().get(i).getName());
					textViewList.get(i).setTextSize(17);
					textViewList.get(i).setId(i);
					textViewList.get(i).setOnLongClickListener(this);
					scrollLayout.addView(textViewList.get(i));
				}
			}
		}
		// TODO add tags in php
		/*
		 * if (type.equals("torrent_tags")) { if(TorrentTabActivity.getTorrentGroup().getStatus()) { for (int i = 0; i <
		 * TorrentTabActivity.getTorrentGroup().getResponse().getTorrents().get(i).get i++) { textViewList.add(new
		 * TextView(this));
		 * textViewList.get(i).setText(ArtistTabActivity.getArtist().getResponse().getTags().get(i).getName());
		 * textViewList.get(i).setTextSize(17); textViewList.get(i).setId(i);
		 * textViewList.get(i).setOnLongClickListener(this); scrollLayout.addView(textViewList.get(i)); } } }
		 */
	}

	private void searchForTag(int i) {

	}

	private void searchForArtist(int i) {

	}

	@Override
	public boolean onLongClick(View v) {
		if (type.equals("artist_tags")) {
			searchForTag(v.getId());
		}
		if (type.equals("artist_similiar")) {
			searchForArtist(v.getId());
		}
		if (type.equals("torrent_tags")) {
			searchForTag(v.getId());
		}
		return false;
	}
}
