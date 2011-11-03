package what.forum;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.thread.Thread;

public class ThreadActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private int counter;
	private ProgressDialog dialog;
	private api.forum.thread.Thread thread;
	private ArrayList<TextView> authorList = new ArrayList<TextView>();
	private ArrayList<WebView> bodyList = new ArrayList<WebView>();
	private Intent intent;
	private int id, page;
	private TextView threadTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.threads);
		threadTitle = (TextView) findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		getBundle();

		new LoadThread().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		page = b.getInt("page");

	}

	private void populateLayout() {
		threadTitle.setText(thread.getResponse().getThreadTitle() + ", page " + thread.getResponse().getCurrentPage());
	}

	private void openOptions(int i) {

	}

	@Override
	public void onClick(View v) {
		if ((v.getId() >= 0) && (counter >= v.getId())) {
		}
	}

	private class LoadThread extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ThreadActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			thread = Thread.threadFromIdAndPage(id, page);
			// TODO fix
			return true;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			populateLayout();
			if (status == false) {
				Toast.makeText(ThreadActivity.this, "Could not load thread", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}
}
