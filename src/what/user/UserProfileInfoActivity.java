package what.user;

import what.gui.ImageLoader;
import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import api.user.User;

public class UserProfileInfoActivity extends MyActivity {
	private static final String AVATAR_STATE_STRING = "Avatar";
	private static final String PROFILE_STATE_STRING = "Profile";
	private ViewFlipper viewFlipper;
	private TextView username;
	private ImageView avatar;
	private Button flipViewButton;
	private WebView profile;
	private Bitmap bmp;
	private User user;
	private Intent intent;
	// false is state 1, true is state 2
	private boolean viewFlipperState = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.userinfo, false);
	}

	@Override
	public void init() {
		user = UserProfileTabActivity.getUser();

	}

	@Override
	public void load() {
		username = (TextView) this.findViewById(R.id.username);
		avatar = (ImageView) this.findViewById(R.id.userAvatar);
		profile = (WebView) this.findViewById(R.id.userProfile);
		viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		flipViewButton = (Button) this.findViewById(R.id.flipViewButton);
	}

	@Override
	public void prepare() {
		populateLayout();
		new LoadImage().execute();
	}

	private void populateLayout() {
		if (user.getStatus()) {
			username.setText(user.getProfile().getUsername());

			String body = user.getProfile().getProfileText();
			profile.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
			profile.getSettings().setSupportZoom(true);
			profile.setVerticalScrollBarEnabled(true);
			profile.setVerticalScrollbarOverlay(true);
			profile.setBackgroundColor(0);
			profile.setBackgroundResource(R.drawable.color_transparent_white);
			if (body.length() > 0) {
				profile.loadData(body, "text/html", "utf-8");
			} else {
				profile.loadData("No profile text", "text/html", "utf-8");
			}
		}
	}

	public void flipView(View v) {
		if (viewFlipperState == true) {
			viewFlipper.showNext();
			flipViewButton.setText(PROFILE_STATE_STRING);
			enableGestures(false);
			viewFlipperState = false;
		} else {
			viewFlipper.showPrevious();
			flipViewButton.setText(AVATAR_STATE_STRING);
			enableGestures(true);
			viewFlipperState = true;
		}
	}

	public void message(View v) {
		Bundle b = new Bundle();
		intent = new Intent(UserProfileInfoActivity.this, what.inbox.NewConversationActivity.class);
		b.putInt("userId", UserProfileTabActivity.getUserId());
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		try {
			bmp.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private class LoadImage extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			// dialog = new ProgressDialog(TorrentInfoActivity.this);
			// dialog.setIndeterminate(true);
			// dialog.setMessage("Loading...");
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String url = user.getProfile().getAvatar();
			if (url.length() > 0) {
				try {
					bmp = ImageLoader.loadBitmap(url);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else
				return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			// dialog.dismiss();
			if (status == true) {
				avatar.setImageBitmap(bmp);
			} else {
				avatar.setImageResource(R.drawable.dne);
			}
			unlockScreenRotation();
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop() */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
