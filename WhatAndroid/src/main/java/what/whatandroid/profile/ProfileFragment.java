package what.whatandroid.profile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.cli.Utils;
import api.soup.MySoup;
import api.user.Profile;
import api.user.User;
import api.user.recent.UserRecents;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 */
public class ProfileFragment extends Fragment implements OnLoggedInCallback {
	/**
	 * The user's profile information
	 */
	private Profile profile;
	/**
	 * The user's recently uploaded/snatched torrents
	 */
	private UserRecents recentTorrents;
	/**
	 * The user id we want to view, passed earlier as a param since we defer loading until onCreate
	 */
	private int userID;
	/**
	 * Callbacks to the activity so we can go set the title
	 */
	private ViewTorrentCallbacks callbacks;
	/**
	 * Various content views displaying the user's information
	 */
	private ImageView avatar;
	/**
	 * The user's stats being shown
	 */
	private TextView username, userClass, upload, download, ratio, paranoia;
	/**
	 * Text views saying what the various numbers in the profile mean, so we can hide those that are hidden
	 * by the user's paranoia
	 */
	private TextView uploadText, downloadText, ratioText, paranoiaText;
	/**
	 * View pagers & adapters for displaying the lists of recent snatches and uploads & headers for the views
	 * headers are needed so we can hide the views if hidden by paranoia
	 */
	private ViewPager recentSnatches, recentUploads;
	private RecentTorrentPagerAdapter snatchesAdapter, uploadsAdapter;
	private View snatchesHeader, uploadsHeader;

	/**
	 * Use this factory method to create a new instance of the fragment displaying the
	 * desired user's profile
	 *
	 * @param id The user id to display the profile of
	 */
	public static ProfileFragment newInstance(int id){
		ProfileFragment fragment = new ProfileFragment();
		fragment.userID = id;
		return fragment;
	}

	public ProfileFragment(){
		// Required empty public constructor
	}

	@Override
	public void onLoggedIn(){
		if (profile == null){
			new LoadProfile().execute(userID);
		}
		else {
			updateProfile();
		}
	}

	/**
	 * Set the user id, should use this if we weren't originally logged in when creating the fragment
	 * since in that case the user id is invalid
	 *
	 * @param id user id to view
	 */
	public void setUserID(int id){
		userID = id;
	}

	/**
	 * Get the user id the fragment is currently viewing
	 *
	 * @return viewed user's id
	 */
	public int getUserID(){
		return userID;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callbacks = (ViewTorrentCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewTorrentCallbacks");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		avatar = (ImageView)view.findViewById(R.id.avatar);
		username = (TextView)view.findViewById(R.id.username);
		userClass = (TextView)view.findViewById(R.id.user_class);
		upload = (TextView)view.findViewById(R.id.upload);
		uploadText = (TextView)view.findViewById(R.id.uploaded_text);
		download = (TextView)view.findViewById(R.id.download);
		downloadText = (TextView)view.findViewById(R.id.downloaded_text);
		ratio = (TextView)view.findViewById(R.id.ratio);
		ratioText = (TextView)view.findViewById(R.id.ratio_text);
		paranoia = (TextView)view.findViewById(R.id.paranoia);
		paranoiaText = (TextView)view.findViewById(R.id.paranoia_text);
		//Hide the paranoia text until we figure out what the user's paranoia settings are
		paranoiaText.setVisibility(View.GONE);
		recentSnatches = (ViewPager)view.findViewById(R.id.recent_snatches);
		snatchesHeader = view.findViewById(R.id.snatches_header);
		recentUploads = (ViewPager)view.findViewById(R.id.recent_uploads);
		uploadsHeader = view.findViewById(R.id.uploads_header);
		return view;
	}

	/**
	 * Update the profile fields with the information we loaded. We need to do a lot null checking here to
	 * properly handle user's various paranoia configurations, which could cause us to get a null for any of the
	 * fields that can be hidden. We also hide the recent snatches/uploads if the user's paranoia is high (6+).
	 * When viewing our own profile we'll get all the data back but will still see our paranoia value so we need to
	 * ignore the paranoia if it's our own profile
	 */
	private void updateProfile(){
		callbacks.setTitle(profile.getUsername());
		username.setText(profile.getUsername());
		userClass.setText(profile.getPersonal().getUserClass());

		//We need to check all the paranoia cases that may cause a field to be missing and hide the views for it
		if (!profile.getAvatar().isEmpty()){
			ImageLoader.getInstance().displayImage(profile.getAvatar(), avatar);
		}
		else {
			avatar.setVisibility(View.GONE);
		}
		if (profile.getPersonal().getParanoia().intValue() > 0 && userID != MySoup.getUserId()){
			paranoiaText.setVisibility(View.VISIBLE);
			paranoia.setText(profile.getPersonal().getParanoiaText());
		}
		else {
			paranoia.setVisibility(View.GONE);
		}
		if (profile.getStats().getUploaded() != null){
			upload.setText(Utils.toHumanReadableSize(profile.getStats().getUploaded().longValue()));
		}
		else {
			uploadText.setVisibility(View.GONE);
			upload.setVisibility(View.GONE);
		}
		if (profile.getStats().getDownloaded() != null){
			download.setText(Utils.toHumanReadableSize(profile.getStats().getDownloaded().longValue()));
		}
		else {
			downloadText.setVisibility(View.GONE);
			download.setVisibility(View.GONE);
		}
		if (profile.getStats().getRatio() != null && profile.getStats().getRequiredRatio() != null){
			ratio.setText(profile.getStats().getRatio() + " / " + profile.getStats().getRequiredRatio());
		}
		else {
			ratioText.setVisibility(View.GONE);
			ratio.setVisibility(View.GONE);
		}
		//TODO: Keep an eye on this API endpoint and watch for when it starts respecting paranoia and we get null back
		if (profile.getPersonal().getParanoia().intValue() < 6 || userID == MySoup.getUserId()){
			if (recentTorrents.getSnatches().size() > 0){
				if (snatchesAdapter == null){
					snatchesAdapter = new RecentTorrentPagerAdapter(recentTorrents.getSnatches(), getChildFragmentManager());
				}
				recentSnatches.setAdapter(snatchesAdapter);
			}
			else {
				snatchesHeader.setVisibility(View.GONE);
				recentSnatches.setVisibility(View.GONE);
			}
			if (recentTorrents.getUploads().size() > 0){
				if (uploadsAdapter == null){
					uploadsAdapter = new RecentTorrentPagerAdapter(recentTorrents.getUploads(), getChildFragmentManager());
				}
				recentUploads.setAdapter(uploadsAdapter);
			}
			else {
				uploadsHeader.setVisibility(View.GONE);
				recentUploads.setVisibility(View.GONE);
			}
		}
		else {
			snatchesHeader.setVisibility(View.GONE);
			recentSnatches.setVisibility(View.GONE);
			recentSnatches.setVisibility(View.GONE);
			uploadsHeader.setVisibility(View.GONE);
			recentUploads.setVisibility(View.GONE);
		}
	}

	/**
	 * Async task to load the user's profile
	 */
	private class LoadProfile extends AsyncTask<Integer, Void, User> {
		/**
		 * Load some user from their id
		 *
		 * @param params params[0] should be the user id to load
		 * @return the user loaded, or null if something went wrong
		 */
		@Override
		protected User doInBackground(Integer... params){
			try {
				User user = User.fromId(params[0]);
				recentTorrents = UserRecents.recentsForUser(params[0]);
				if (user.getStatus() && recentTorrents != null && recentTorrents.getStatus()){
					return user;
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
		protected void onPostExecute(User user){
			if (getActivity() != null){
				getActivity().setProgressBarIndeterminateVisibility(false);
				getActivity().setProgressBarIndeterminate(false);
			}
			if (user != null){
				profile = user.getProfile();
				updateProfile();
			}
			else {
				Toast.makeText(getActivity(), "Failed to load user", Toast.LENGTH_LONG).show();
			}
		}
	}
}
