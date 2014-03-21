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
import what.whatandroid.callbacks.ViewTorrentCallbacks;

/**
 */
public class ProfileFragment extends Fragment {
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
	private TextView username, userClass, upload, download, ratio, requiredRatio, paranoiaText;
	private ViewPager recentSnatches, recentUploads;

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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (profile == null){
			new LoadProfile().execute(userID);
		}
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
		download = (TextView)view.findViewById(R.id.download);
		ratio = (TextView)view.findViewById(R.id.ratio);
		requiredRatio = (TextView)view.findViewById(R.id.required_ratio);
		paranoiaText = (TextView)view.findViewById(R.id.paranoia_text);
		recentSnatches = (ViewPager)view.findViewById(R.id.recent_snatches);
		recentUploads = (ViewPager)view.findViewById(R.id.recent_uploads);
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
		//No empty in API 8? Android Studio warns that API 9 is required
		if (!profile.getAvatar().equalsIgnoreCase("")){
			ImageLoader.getInstance().displayImage(profile.getAvatar(), avatar);
		}
		else {
			avatar.setVisibility(View.GONE);
		}
		username.setText(profile.getUsername());
		userClass.setText(profile.getPersonal().getUserClass());
		if (profile.getPersonal().getParanoia().intValue() > 0 && userID != MySoup.getUserId()){
			paranoiaText.setText("Paranoia: " + profile.getPersonal().getParanoiaText());
		}
		else {
			paranoiaText.setVisibility(View.GONE);
		}
		if (profile.getStats().getUploaded() != null){
			upload.setText("Up: " + Utils.toHumanReadableSize(profile.getStats().getUploaded().longValue()));
		}
		else {
			upload.setVisibility(View.GONE);
		}
		if (profile.getStats().getDownloaded() != null){
			download.setText("Down: " + Utils.toHumanReadableSize(profile.getStats().getDownloaded().longValue()));
		}
		else {
			download.setVisibility(View.GONE);
		}
		//TODO: These fields will be merged soon
		if (profile.getStats().getRatio() != null && profile.getStats().getRequiredRatio() != null){
			ratio.setText("Ratio: " + String.format("%.2f", profile.getStats().getRatio().floatValue()));
			requiredRatio.setText("Required: " + profile.getStats().getRequiredRatio().toString());
			colorizeRatio();
		}
		else {
			ratio.setVisibility(View.GONE);
			requiredRatio.setVisibility(View.GONE);
		}
		//TODO: Keep an eye on this API endpoint and watch for when it starts respecting paranoia and we get null back
		if (profile.getPersonal().getParanoia().intValue() < 6 || userID == MySoup.getUserId()){
			recentSnatches.setAdapter(new RecentTorrentPagerAdapter(recentTorrents.getSnatches(),
				getActivity().getSupportFragmentManager()));
			recentUploads.setAdapter(new RecentTorrentPagerAdapter(recentTorrents.getUploads(),
				getActivity().getSupportFragmentManager()));
		}
		else {
			recentSnatches.setVisibility(View.GONE);
			recentUploads.setVisibility(View.GONE);
		}
	}

	/**
	 * Colorize the ratio text view based on its distance from the required ratio
	 */
	private void colorizeRatio(){
		float diff = profile.getStats().getRatio().floatValue()
			- profile.getStats().getRequiredRatio().floatValue();
		if (diff < 0.1){
			ratio.setTextColor(getResources().getColor(R.color.Red));
		}
		else if (diff < 0.5){
			ratio.setTextColor(getResources().getColor(R.color.Yellow));
		}
		else if (diff < 1.0){
			ratio.setTextColor(getResources().getColor(R.color.Orange));
		}
		else {
			ratio.setTextColor(getResources().getColor(R.color.Green));
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
			getActivity().setProgressBarIndeterminateVisibility(true);
			getActivity().setProgressBarIndeterminate(true);
		}

		@Override
		protected void onPostExecute(User user){
			getActivity().setProgressBarIndeterminateVisibility(false);
			getActivity().setProgressBarIndeterminate(false);
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
