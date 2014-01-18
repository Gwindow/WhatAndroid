package what.whatandroid.profile;

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
import api.user.User;
import api.user.recent.UserRecents;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;

/**
 */
public class ProfileFragment extends Fragment {
	/**
	 * The user we're viewing
	 */
	private User user;
	/**
	 * The user's recently uploaded/snatched torrents
	 */
	private UserRecents recentTorrents;
	/** The user id we want to view, passed earlier as a param since we defer loading until onCreate */
	private int userID;
	/** Various content views displaying the user's information */
	private ImageView avatar;
	private TextView username, userClass, upload, download, ratio, requiredRatio;
	private ViewPager recentUploads;

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
		if (user == null){
			new LoadProfile().execute(userID);
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
		recentUploads = (ViewPager)view.findViewById(R.id.recent_uploads);
		return view;
	}

	/**
	 * Update the profile fields with the information we loaded
	 */
	void updateProfile(){
		//TODO: We need to check the user paranoia and update what we can see based on that
		ImageLoader.getInstance().displayImage(user.getProfile().getAvatar(), avatar);
		username.setText(user.getProfile().getUsername());
		userClass.setText(user.getProfile().getPersonal().getUserClass());
		upload.setText("Up: " + Utils.toHumanReadableSize(user.getProfile().getStats().getUploaded().longValue()));
		download.setText("Down: " + Utils.toHumanReadableSize(user.getProfile().getStats().getDownloaded().longValue()));
		ratio.setText("Ratio: " + user.getProfile().getStats().getRatio().toString());
		requiredRatio.setText("Required: " + user.getProfile().getStats().getRequiredRatio().toString());
		recentUploads.setAdapter(new RecentTorrentPagerAdapter(recentTorrents.getUploads(),
			getActivity().getSupportFragmentManager()));
		colorizeRatio();
	}

	/**
	 * Colorize the ratio text view based on its distance from the required ratio
	 * TODO: should we keep this?
	 */
	void colorizeRatio(){
		float diff = user.getProfile().getStats().getRatio().floatValue()
			- user.getProfile().getStats().getRequiredRatio().floatValue();
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
	private class LoadProfile extends AsyncTask<Integer, Void, Boolean> {
		/**
		 * Load some user from their id
		 *
		 * @param params params[0] should be the user id to load
		 * @return the user loaded, or null if something went wrong
		 */
		@Override
		protected Boolean doInBackground(Integer... params){
			try {
				user = User.userFromId(params[0]);
				recentTorrents = UserRecents.recentsForUser(params[0]);
				if (user != null && user.getStatus() && recentTorrents != null && recentTorrents.getStatus()){
					return true;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (status){
				updateProfile();
			}
			else {
				Toast.makeText(getActivity(), "Failed to load user", Toast.LENGTH_LONG).show();
			}
		}
	}
}
