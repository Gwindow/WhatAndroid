package what.whatandroid.torrentgroup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.torrents.torrents.EditionTorrents;
import api.torrents.torrents.TorrentGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

import java.util.List;

/**
 * Fragment for viewing a torrent group's information
 */
public class TorrentGroupFragment extends Fragment implements OnLoggedInCallback {
	/**
	 * The torrent group being viewed
	 */
	private TorrentGroup group;
	/**
	 * The torrent group id, passed to us when creating the fragment so we can load
	 * the group later, when the fragment is actually created
	 */
	private int groupID;
	/**
	 * Callbacks to the parent activity for setting the title
	 */
	private SetTitleCallback callbacks;
	/**
	 * Various content views displaying the group information
	 */
	private ImageView image;
	private View imageHeader, titleHeader;
	private ProgressBar spinner;
	private TextView albumTitle;
	private ExpandableListView torrentList;

	/**
	 * Use this factory method to create a new torrent group fragment displaying the
	 * torrents in the group with id passed
	 *
	 * @param id torrent group id to show
	 * @return TorrentGroupFragment displaying the group
	 */
	public static TorrentGroupFragment newInstance(int id){
		TorrentGroupFragment fragment = new TorrentGroupFragment();
		fragment.groupID = id;
		return fragment;
	}

	public static TorrentGroupFragment newInstance(TorrentGroup group){
		TorrentGroupFragment fragment = new TorrentGroupFragment();
		fragment.group = group;
		fragment.groupID = group.getId();
		return fragment;
	}

	public TorrentGroupFragment(){
		//Required empty ctor
	}

	public TorrentGroup getGroup(){
		return group;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallbacks");
		}
	}

	@Override
	public void onLoggedIn(){
		if (group == null){
			new LoadGroup().execute(groupID);
		}
		else {
			updateTorrentGroup(group.getEditions());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		torrentList = (ExpandableListView)view.findViewById(R.id.exp_list);
		titleHeader = inflater.inflate(R.layout.header_album_title, null);
		albumTitle = (TextView)titleHeader.findViewById(R.id.title);
		imageHeader = inflater.inflate(R.layout.header_image, null);
		image = (ImageView)imageHeader.findViewById(R.id.image);
		spinner = (ProgressBar)imageHeader.findViewById(R.id.loading_indicator);
		return view;
	}

	/**
	 * Update all the torrent group information being shown after loading
	 */
	private void updateTorrentGroup(List<EditionTorrents> editions){
		callbacks.setTitle(group.getResponse().getGroup().getName());

		String imgUrl = group.getResponse().getGroup().getWikiImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner));
			torrentList.addHeaderView(imageHeader);
		}
		else {
			imageHeader.setVisibility(View.GONE);
			spinner.setVisibility(View.GONE);
		}
		albumTitle.setText(group.getResponse().getGroup().getName());
		torrentList.addHeaderView(titleHeader);

		TorrentGroupAdapter adapter = new TorrentGroupAdapter(getActivity(), getChildFragmentManager(),
			group.getResponse().getGroup().getMusicInfo(), editions);
		torrentList.setAdapter(adapter);
		torrentList.setOnChildClickListener(adapter);
	}

	/**
	 * Async task to load the torrent group info
	 */
	private class LoadGroup extends AsyncTask<Integer, Void, TorrentGroup> {
		List<EditionTorrents> editions;

		/**
		 * Load some torrent group from its id
		 *
		 * @param params params[0] should contain the id to load
		 * @return the loaded group, or null if something went wrong
		 */
		@Override
		protected TorrentGroup doInBackground(Integer... params){
			try {
				TorrentGroup tg = TorrentGroup.fromId(params[0]);
				if (tg != null && tg.getStatus()){
					editions = tg.getEditions();
					return tg;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute(){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(true);
				getActivity().setProgressBarIndeterminate(true);
			}
		}

		@Override
		protected void onPostExecute(TorrentGroup torrentGroup){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(false);
				getActivity().setProgressBarIndeterminate(false);
			}
			if (torrentGroup != null){
				group = torrentGroup;
				updateTorrentGroup(editions);
			}
			else {
				Toast.makeText(getActivity(), "Failed to load torrent group", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
