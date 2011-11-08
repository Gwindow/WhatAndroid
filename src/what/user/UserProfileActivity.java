package what.user;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import api.soup.MySoup;
import api.user.User;

public class UserProfileActivity extends MyActivity {
	private int userId;
	private User user;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.userprofile);
		getBundle();
		new LoadUser().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		try {
			userId = b.getInt("userId");
		} catch (Exception e) {
			// if for some reason the user id isn't received set it to your own id so everything doesnt crash
			userId = Integer.parseInt(MySoup.getUserId());
		}
	}

	private void populateLayout() {

	}

	private class LoadUser extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(UserProfileActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			user = User.userFromId(userId);
			return user.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(UserProfileActivity.this, "Could not load user profile", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
