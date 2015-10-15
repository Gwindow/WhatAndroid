package what.whatandroid.artist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;


import api.torrents.artist.Artist;
import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.views.ImageDialog;

/**
 * Fragment displaying the artist image and list of releases and requests
 */
public class ArtistReleasesFragment extends Fragment implements LoadingListener<Artist>, View.OnClickListener {
	/**
	 * The artist being shown
	 */
	private Artist artist;
	/**
	 * Various content views displaying the artist information
	 */
	private ImageView image;
	private ProgressBar spinner;
	private View artContainer;
	private ExpandableListView torrentList;

	public ArtistReleasesFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);
		View header = inflater.inflate(R.layout.header_image, torrentList, false);
		torrentList.addHeaderView(header);
		image = (ImageView)header.findViewById(R.id.image);
		image.setOnClickListener(this);
		spinner = (ProgressBar)header.findViewById(R.id.loading_indicator);
		artContainer = header.findViewById(R.id.art_container);
		if (artist != null){
			populateViews();
		}
		return view;
	}

	/**
	 * Update all the artist information with the loaded api request
	 */
	private void populateViews(){
		((SetTitleCallback)getActivity()).setTitle(artist.getResponse().getName());
		String imgUrl = artist.getResponse().getImage();
		if (!SettingsActivity.imagesEnabled(getContext())) {
			artContainer.setVisibility(View.GONE);
		} else {
			artContainer.setVisibility(View.VISIBLE);
			WhatApplication.loadImage(getContext(), imgUrl, image, spinner, null, null);
		}
		if (torrentList.getAdapter() == null){
			ArtistTorrentAdapter adapter = new ArtistTorrentAdapter(getActivity(), artist.getReleases().flatten(),
				artist.getResponse().getRequests());
			torrentList.setAdapter(adapter);
			torrentList.setOnChildClickListener(adapter);
		}
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(artist.getResponse().getImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
	}

	@Override
	public void onLoadingComplete(Artist data){
		artist = data;
		if (isAdded()){
			populateViews();
		}
	}
}
