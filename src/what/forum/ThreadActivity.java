package what.forum;

import java.util.ArrayList;
import java.util.List;

import what.gui.ImageLoader;
import what.gui.MyActivity;
import what.gui.R;
import what.settings.Settings;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.thread.Posts;
import api.forum.thread.Thread;
import api.util.Triple;

public class ThreadActivity extends MyActivity implements OnLongClickListener {
	private ScrollView scrollView;
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private api.forum.thread.Thread thread;
	private Intent intent;
	private int id, page, postId;
	private TextView threadTitle;
	private Button backButton, nextButton, lastButton, replyButton;
	private ArrayList<RelativeLayout> listOfPosts = new ArrayList<RelativeLayout>();
	private boolean hasAvatarsEnabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.posts, true);

		hasAvatarsEnabled = Settings.getAvatarsEnabled();

		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		threadTitle = (TextView) this.findViewById(R.id.titleText);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);
		lastButton = (Button) this.findViewById(R.id.lastButton);
		replyButton = (Button) this.findViewById(R.id.replyButton);

		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		setButtonState(lastButton, false);
		setButtonState(replyButton, false);

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
	}

	@SuppressWarnings("unchecked")
	private void populateLayout() {
		setButtonState(backButton, thread.hasPreviousPage());
		setButtonState(nextButton, thread.hasNextPage());
		setButtonState(lastButton, thread.hasNextPage());
		setButtonState(replyButton, !thread.getResponse().isLocked());
		threadTitle.setText(thread.getResponse().getThreadTitle() + ", page " + thread.getResponse().getCurrentPage());
		List<Posts> posts = thread.getResponse().getPosts();
		RelativeLayout layout;
		TextView username, time;
		WebView body;
		for (int i = 0; i < posts.size(); i++) {
			layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.post, null);
			RelativeLayout relativeLayout = (RelativeLayout) layout.findViewById(R.id.content);

			username = (TextView) layout.findViewById(R.id.username);
			username.setText(posts.get(i).getAuthor().getAuthorName());
			time = (TextView) layout.findViewById(R.id.time);
			time.setText(posts.get(i).getAddedTime());
			body = (WebView) layout.findViewById(R.id.body);
			body.loadData(posts.get(i).getBody().trim(), "text/html", "utf-8");

			body.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
			body.getSettings().setSupportZoom(true);
			body.setVerticalScrollBarEnabled(true);
			body.setVerticalScrollbarOverlay(true);

			if ((i % 2) == 0) {
				relativeLayout.setBackgroundResource(R.drawable.color_transparent_white);
				body.setBackgroundColor(0);
			} else {
				relativeLayout.setBackgroundResource(R.drawable.color_transparent_light_gray);
				body.setBackgroundColor(0);
			}

			listOfPosts.add(layout);
			listOfPosts.get(i).setId(i);
			listOfPosts.get(i).setClickable(true);
			listOfPosts.get(i).setOnLongClickListener(this);
			scrollLayout.addView(listOfPosts.get(i));
			if (hasAvatarsEnabled) {
				new LoadAvatar().execute(new Triple<Integer, Integer, String>(i, posts.get(i).getAuthor().getAuthorId()
						.intValue(), posts.get(i).getAuthor().getAvatar()));
			} else {
				listOfPosts.get(i).findViewById(R.id.avatar).setVisibility(ImageView.GONE);
			}
			/*
			 * ImageView a; a = (ImageView) listOfPosts.get(i).findViewById(R.id.avatar); if (a.getHeight() >
			 * body.getHeight()) { LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, a.getHeight());
			 * body.setLayoutParams(lp); }
			 */
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

	public void showThreadInfo(View v) {
		/*
		 * Bundle b = new Bundle(); intent = new Intent(ThreadActivity.this, what.forum.ThreadInfoActivity.class);
		 * b.putInt("id", id); b.putBoolean("subscribed", thread.getResponse().isSubscribed()); intent.putExtras(b);
		 * startActivityForResult(intent, 0);
		 */
	}

	@Override
	public void onRightGesturePerformed() {
		next(null);
	}

	@Override
	public void onLeftGesturePerformed() {
		back(null);
	}

	@Override
	public void onDownGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);

	}

	@Override
	public boolean onLongClick(View v) {
		for (int i = 0; i < listOfPosts.size(); i++) {
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

	private class LoadAvatar extends AsyncTask<Triple<Integer, Integer, String>, Void, Triple<Boolean, Integer, Bitmap>> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Triple<Boolean, Integer, Bitmap> doInBackground(Triple<Integer, Integer, String>... params) {
			Bitmap bitmap;
			int pos = params[0].getA().intValue();
			int id = params[0].getB();
			String url = params[0].getC();
			// if (!ImageCache.hasImage(id, url)) {
			if (url.length() > 0) {
				try {
					bitmap = ImageLoader.loadBitmap(url);
					// ImageCache.saveImage(id, url, bitmap);
					// Log.v("cache", "Image saved");
					return new Triple<Boolean, Integer, Bitmap>(true, pos, bitmap);
				} catch (Exception e) {
					e.printStackTrace();
					return new Triple<Boolean, Integer, Bitmap>(false, pos, null);
				}
			}
			/*
			 * } else { Log.v("cache", "Image loaded"); return new Triple<Boolean, Integer, Bitmap>(true, pos,
			 * ImageCache.getImage(id)); }
			 */
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

	public void reply(View v) {
		replyDialog();
	}

	private void replyDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("Reply");

		final EditText input = new EditText(this);
		input.setGravity(Gravity.TOP);
		input.setGravity(Gravity.LEFT);
		input.setMinHeight(this.getHeight() / 3);
		input.setMinWidth(this.getWidth() / 2);
		input.setText(QuoteBuffer.getBuffer());
		alert.setView(input);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (input.getText().length() > 0) {
					new PostReply().execute(input.getText().toString());
				} else {
					Toast.makeText(ThreadActivity.this, "Enter a reply", Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
	}

	private class PostReply extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ThreadActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Replying...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Thread.postReply(id, params[0]);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				Toast.makeText(ThreadActivity.this, "Reply posted", Toast.LENGTH_SHORT).show();
			}
			if (status == false) {
				Toast.makeText(ThreadActivity.this, "Could not post reply", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

}
