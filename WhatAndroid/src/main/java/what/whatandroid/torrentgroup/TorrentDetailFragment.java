package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import api.cli.Utils;
import api.soup.MySoup;
import api.torrents.torrents.Torrents;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.comments.WhatBBParser;

import java.util.Date;

/**
 * A fragment showing a detailed view of a torrent
 */
public class TorrentDetailFragment extends Fragment implements LoadingListener<Torrents>, View.OnClickListener {
	/**
	 * The torrent being displayed
	 */
	private Torrents torrent;
	/**
	 * Callbacks to view the user who uploaded the torrent
	 */
	private ViewUserCallbacks viewUser;
	/**
	 * Views displaying the torrent information
	 */
	private TextView editionTitle, format, size,
		snatches, leechers, seeders, uploadDate, uploader, description, folderTitle;
	private View freeleech, reported;
	private ListView fileList;
	private TorrentFilesAdapter adapter;

	public static TorrentDetailFragment newInstance(Torrents torrent){
		TorrentDetailFragment f = new TorrentDetailFragment();
		f.torrent = torrent;
		return f;
	}

	public TorrentDetailFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			viewUser = (ViewUserCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewUserCallbacks");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		fileList = (ListView)view.findViewById(R.id.list);
		View header = inflater.inflate(R.layout.header_torrent_detail, null);
		fileList.addHeaderView(header, null, false);
		adapter = new TorrentFilesAdapter(getActivity());
		fileList.setAdapter(adapter);

		editionTitle = (TextView)header.findViewById(R.id.edition_title);
		format = (TextView)header.findViewById(R.id.format);
		size = (TextView)header.findViewById(R.id.size);
		snatches = (TextView)header.findViewById(R.id.snatches);
		leechers = (TextView)header.findViewById(R.id.leechers);
		seeders = (TextView)header.findViewById(R.id.seeders);
		uploadDate = (TextView)header.findViewById(R.id.uploaded_date);
		uploader = (TextView)header.findViewById(R.id.uploaded_by);
		uploader.setOnClickListener(this);
		description = (TextView)header.findViewById(R.id.description);
		folderTitle = (TextView)header.findViewById(R.id.folder_title);
		freeleech = header.findViewById(R.id.freeleech_icon);
		reported = header.findViewById(R.id.reported_icon);
		if (torrent != null){
			populateView();
		}
		return view;
	}

	@Override
	public void onClick(View v){
		viewUser.viewUser(torrent.getUserId().intValue());
	}

	@Override
	public void onLoadingComplete(Torrents t){
		torrent = t;
		if (isAdded()){
			populateView();
		}
	}

	void populateView(){
		editionTitle.setText(torrent.getEditionTitle());
		format.setText(torrent.getMediaFormatEncoding());
		size.setText(Utils.toHumanReadableSize(torrent.getSize().longValue()));
		snatches.setText(torrent.getSnatched().toString());
		leechers.setText(torrent.getLeechers().toString());
		seeders.setText(torrent.getSeeders().toString());
		uploader.setText(torrent.getUsername());
		folderTitle.setText("/" + torrent.getFilePath() + "/");
		Date uploaded = MySoup.parseDate(torrent.getTime());
		uploadDate.setText("Uploaded " + DateUtils.getRelativeTimeSpanString(uploaded.getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

		if (!torrent.isFreeTorrent()){
			freeleech.setVisibility(View.GONE);
		}
		if (!torrent.isReported()){
			reported.setVisibility(View.GONE);
		}
		if (!torrent.getDescription().isEmpty()){
			//We'd probably want to do this parsing in the background as well? How would we pass it
			//to the views from the loader then?
			description.setText(WhatBBParser.parsebb(torrent.getDescription()));
		}
		else {
			description.setVisibility(View.GONE);
		}
		adapter.addAll(torrent.getTorrentFiles());
		adapter.notifyDataSetChanged();
	}
}
