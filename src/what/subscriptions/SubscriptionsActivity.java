package what.subscriptions;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.subscriptions.Subscriptions;
import api.subscriptions.Threads;

public class SubscriptionsActivity extends MyActivity implements OnClickListener {
	private ArrayList<TextView> threadList;
	private LinearLayout scrollLayout;
	private Subscriptions subscriptions;
	private ProgressDialog dialog;
	private int counter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.subscriptions, true);
	}

	@Override
	public void init() {
		threadList = new ArrayList<TextView>();
	}

	@Override
	public void load() {
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
	}

	@Override
	public void prepare() {
		new LoadSubscriptions().execute();
	}

	private void populateLayout() {
		List<Threads> threads = subscriptions.getResponse().getThreads();
		for (int i = 0; i < threads.size(); i++) {
			if ((i % 2) == 0) {
				threadList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				threadList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			threadList.get(i).setText(threads.get(i).getForumName() + " > " + threads.get(i).getThreadTitle());
			threadList.get(i).setId(i);
			threadList.get(i).setOnClickListener(this);
			scrollLayout.addView(threadList.get(i));
			counter++;
		}
	}

	private void openThread(int i) {

	}

	@Override
	public void onClick(View v) {
		if ((v.getId() >= 0) && (counter >= v.getId())) {
			openThread(v.getId());
		}
	}

	private class LoadSubscriptions extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(SubscriptionsActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			subscriptions = Subscriptions.init();
			// return artist.getStatus();
			// TODO fix
			return true;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			populateLayout();
			if (status == false) {
				Toast.makeText(SubscriptionsActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
