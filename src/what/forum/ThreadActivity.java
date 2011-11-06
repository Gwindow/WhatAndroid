package what.forum;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.thread.Posts;
import api.forum.thread.Thread;

public class ThreadActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private int counter;
	private ProgressDialog dialog;
	private api.forum.thread.Thread thread;
	private Intent intent;
	private int id, page;
	private TextView threadTitle;
	private Button backButton, nextButton;
	private ArrayList<RelativeLayout> listOfPosts = new ArrayList<RelativeLayout>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts);
		threadTitle = (TextView) findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);
		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		getBundle();

		new LoadThread().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		page = b.getInt("page");

	}

	private void populateLayout() {
		setButtonState(backButton, thread.hasPreviousPage());
		setButtonState(nextButton, thread.hasNextPage());
		threadTitle.setText(thread.getResponse().getThreadTitle() + ", page " + thread.getResponse().getCurrentPage());
		List<Posts> posts = thread.getResponse().getPosts();
		RelativeLayout layout;
		TextView username;
		WebView body;
		for (int i = 0; i < posts.size(); i++) {
			layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.post, null);
			username = (TextView) layout.findViewById(R.id.username);
			username.setText(posts.get(i).getAuthor().getAuthorName());
			body = (WebView) layout.findViewById(R.id.post);
			body.loadData(posts.get(i).getBody(), "text/html", "utf-8");
			body.setBackgroundColor(R.drawable.btn_black);
			listOfPosts.add(layout);
			scrollLayout.addView(listOfPosts.get(i));
		}
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
			return thread.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(ThreadActivity.this, "Could not load thread", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
