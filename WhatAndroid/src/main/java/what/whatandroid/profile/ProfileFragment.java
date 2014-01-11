package what.whatandroid.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import api.user.User;
import what.whatandroid.R;

/**
 */
public class ProfileFragment extends Fragment {
	/** The user */
	User user;
	/** The user id we want to view, passed earlier as a param since we defer loading until onCreate */
	int userID;
	/** The viewholder containing the views in the fragment */
	ViewHolder holder;

    /**
     * Use this factory method to create a new instance of the fragment displaying the
	 * desired user's profile
	 * @param id The user id to display the profile of
     */
    public static ProfileFragment newInstance(int id) {
		ProfileFragment fragment = new ProfileFragment();
		fragment.userID = id;
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (user == null){
			new LoadProfile().execute(userID);
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		holder = new ViewHolder();
		holder.username = (TextView)view.findViewById(R.id.username);
		holder.username.setText("Username loading....");

		return view;
    }

	/**
	 * Update the profile fields with the information we loaded
	 */
	void updateProfile(){
		holder.username.setText(user.getProfile().getUsername());
	}

	/**
	 * Async task to load the user's profile
	 */
	private class LoadProfile extends AsyncTask<Integer, Void, User> {
		/**
		 * Load some user from their id
		 * @param params params[0] should be the user id to load
		 * @return the user loaded, or null if something went wrong
		 */
		@Override
		protected User doInBackground(Integer... params) {
			try {
				User user = User.userFromId(params[0]);
				if (user != null && user.getStatus()){
					return user;
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(User u) {
			if (u != null){
				user = u;
				updateProfile();
			}
			else {
				Toast.makeText(getActivity(), "Failed to load user", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Holds the various views we want to keep updated
	 */
	private static class ViewHolder {
		TextView username;
	}
}
