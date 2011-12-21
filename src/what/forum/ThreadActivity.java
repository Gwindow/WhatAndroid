package what.forum;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.PatchInputStream;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.thread.Posts;
import api.forum.thread.Thread;
import api.util.Triple;
import api.util.Tuple;

public class ThreadActivity extends MyActivity implements OnLongClickListener {
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private api.forum.thread.Thread thread;
	private Intent intent;
	private int id, page, postId;
	private TextView threadTitle;
	private Button backButton, nextButton, lastButton;
	private ArrayList<RelativeLayout> listOfPosts = new ArrayList<RelativeLayout>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts);

		threadTitle = (TextView) findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);
		lastButton = (Button) this.findViewById(R.id.lastButton);
		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		setButtonState(lastButton, false);

		getBundle();

		new LoadThread().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		try {
			page = b.getInt("page");
		} catch (Exception e) {
			page = 0;
			e.printStackTrace();
		}
		try {
			postId = b.getInt("postId");
		} catch (Exception e) {
			postId = 0;
			e.printStackTrace();
		}
		Toast.makeText(this, String.valueOf(id) + "," + String.valueOf(postId), Toast.LENGTH_LONG).show();
	}

	private void populateLayout() {
		setButtonState(backButton, thread.hasPreviousPage());
		setButtonState(nextButton, thread.hasNextPage());
		setButtonState(lastButton, thread.hasNextPage());
		threadTitle.setText(thread.getResponse().getThreadTitle() + ", page " + thread.getResponse().getCurrentPage());
		List<Posts> posts = thread.getResponse().getPosts();
		RelativeLayout layout;
		TextView username, time;
		WebView body;
		for (int i = 0; i < posts.size(); i++) {
			layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.post, null);
			username = (TextView) layout.findViewById(R.id.username);
			username.setText(posts.get(i).getAuthor().getAuthorName());
			time = (TextView) layout.findViewById(R.id.time);
			time.setText(posts.get(i).getAddedTime());
			body = (WebView) layout.findViewById(R.id.post);
			// TODO if statement to check if avatars enabled

			// TODO confirm that the following 2 lines fix large image loading
			// body.getSettings().setLoadWithOverviewMode(true);
			body.getSettings().setUseWideViewPort(true);
			body.loadData(posts.get(i).getBody(), "text/html", "utf-8");
			// body.setBackgroundColor(R.drawable.btn_black);
			listOfPosts.add(layout);
			listOfPosts.get(i).setId(i);
			listOfPosts.get(i).setClickable(true);
			listOfPosts.get(i).setOnLongClickListener(this);
			scrollLayout.addView(listOfPosts.get(i));
			new LoadAvatar().execute(new Tuple<Integer, String>(i, posts.get(i).getAuthor().getAvatar()));
		}
	}

	private void openOptions(int i) {
		Bundle b = new Bundle();
		intent = new Intent(ThreadActivity.this, what.forum.PostOptionsActivity.class);
		b.putInt("threadId", thread.getResponse().getThreadId().intValue());
		b.putString("post", thread.getResponse().getPosts().get(i).getQuotableBody());
		b.putInt("userId", thread.getResponse().getPosts().get(i).getAuthor().getAuthorId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void back(View v) {
		if (thread.hasPreviousPage()) {
			Bundle b = new Bundle();
			intent = new Intent(ThreadActivity.this, what.forum.ThreadActivity.class);
			b.putInt("id", id);
			b.putInt("page", page - 1);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		} else {
			finish();
		}
	}

	public void next(View v) {
		if (thread.hasNextPage()) {
			Bundle b = new Bundle();
			intent = new Intent(ThreadActivity.this, what.forum.ThreadActivity.class);
			b.putInt("id", id);
			b.putInt("page", page + 1);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}
	}

	public void last(View v) {
		if (thread.hasNextPage()) {
			Bundle b = new Bundle();
			intent = new Intent(ThreadActivity.this, what.forum.ThreadActivity.class);
			b.putInt("id", id);
			b.putInt("page", thread.getLastPage());
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {
				next(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				back(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		for (int i = 0; i < (listOfPosts.size()); i++) {
			if (v.getId() == listOfPosts.get(i).getId()) {
				openOptions(i);
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		QuoteBuffer.clear();
		ImageView a;
		for (int i = 0; i < listOfPosts.size(); i++) {
			try {
				a = (ImageView) listOfPosts.get(i).findViewById(R.id.avatar);
				a.destroyDrawingCache();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		ImageView a;
		for (int i = 0; i < listOfPosts.size(); i++) {
			try {
				a = (ImageView) listOfPosts.get(i).findViewById(R.id.avatar);
				a.destroyDrawingCache();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onPause();
	}

	private class LoadAvatar extends AsyncTask<Tuple<Integer, String>, Void, Triple<Boolean, Integer, Bitmap>> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Triple<Boolean, Integer, Bitmap> doInBackground(Tuple<Integer, String>... params) {
			Bitmap b;
			URL url;
			String s = params[0].getB();
			int pos = params[0].getA().intValue();
			if (s.length() > 0) {
				try {
					url = new URL(s);
					b = BitmapFactory.decodeStream(new PatchInputStream(url.openStream()));
					return new Triple<Boolean, Integer, Bitmap>(true, pos, b);
				} catch (Exception e) {
					e.printStackTrace();
					return new Triple<Boolean, Integer, Bitmap>(false, pos, null);
				}
			}
			return new Triple<Boolean, Integer, Bitmap>(false, pos, null);
		}

		@Override
		protected void onPostExecute(Triple<Boolean, Integer, Bitmap> t) {
			ImageView a;
			a = (ImageView) listOfPosts.get(t.getB()).findViewById(R.id.avatar);
			if (t.getA() == true) {
				a.setImageBitmap(t.getC());
			} else {
				a.setImageResource(R.drawable.dne);
			}
		}

	}

	private class LoadAvatars extends AsyncTask<Void, Void, List<Bitmap>> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<Bitmap> doInBackground(Void... params) {
			ArrayList<Bitmap> list = new ArrayList<Bitmap>();
			for (int i = 0; i < thread.getResponse().getPosts().size(); i++) {
				URL url;
				String s = thread.getResponse().getPosts().get(i).getAuthor().getAvatar();
				if (s.length() > 0) {
					try {
						url = new URL(s);
						list.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
					} catch (Exception e) {

					}
				}
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Bitmap> list) {
			for (int i = 0; i < list.size(); i++) {
				ImageView a;
				a = (ImageView) listOfPosts.get(i).findViewById(R.id.avatar);
				try {
					a.setImageBitmap(list.get(i));
				} catch (Exception e) {
					a.setImageResource(R.drawable.dne);
					e.printStackTrace();
				}
			}
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
			// load from last post id
			if ((page == 0) && (postId != 0)) {
				thread = Thread.threadFromIdAndPostId(id, postId);
				Log.v("thread test", "load from last post id");
			}
			// load from page number
			else if ((postId == 0) && (page != 0)) {
				thread = Thread.threadFromIdAndPage(id, page);
				Log.v("thread test", "load from last page");

			}
			// if everything goes wrong load from the first page
			else {
				thread = Thread.threadFromFirstPage(id);
				Log.v("thread test", "load from first page");

			}
			return thread.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				page = thread.getResponse().getCurrentPage().intValue();
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
