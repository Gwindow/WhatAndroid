package what.whatandroid.home;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import api.cli.Utils;
import api.index.Index;
import what.whatandroid.R;

/**
 * Home fragment shows a user a summary of their stats, subscriptions
 * and notifications.
 */
public class HomeFragment extends Fragment {
	private Index index;
	private TextView username, uploaded;

	public static String NAME = "Home";

	public HomeFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.home_layout, container, false);
		username = (TextView)v.findViewById(R.id.username_display);
		uploaded = (TextView)v.findViewById(R.id.uploaded_stat);

		new LoadIndex().execute();
		return v;
	}

	private class LoadIndex extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				index = Index.init();
				return true;
			}
			catch (Exception e){
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status){
				username.setText(index.getResponse().getUsername());
				uploaded.setText("Uploaded: " + Utils.toHumanReadableSize(
					index.getResponse().getUserstats().getUploaded().longValue()));
			}
			else {
				Toast.makeText(getActivity(), "Failed to load index", Toast.LENGTH_LONG).show();
			}
		}


	}
}