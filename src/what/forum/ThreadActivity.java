package what.forum;

import java.util.LinkedList;

import what.gui.ActivityNames;
import what.gui.AsyncImageGetter;
import what.gui.BundleKeys;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.ReplyActivity;
import what.gui.Scrollable;
import what.user.UserActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.thread.Posts;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 8, 2012 1:08:36 AM
 */
public class ThreadActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int REPLY_TAG = 0;
	private static final int QUOTE_TAG = 1;
	private static final int USER_TAG = 2;
	private static final int NON_EXISTANT = -1;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private api.forum.thread.Thread thread;
	private int threadPage;
	private int threadId;
	private int lastReadPostId;
	private boolean isLoaded;

	private LinkedList<Posts> posts;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.FORUM);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.generic_endless_scrollview, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		threadId = bundle.getInt(BundleKeys.THREAD_ID);
		if (bundle.containsKey(BundleKeys.THREAD_LAST_READ_POST_ID)) {
			lastReadPostId = bundle.getInt(BundleKeys.THREAD_LAST_READ_POST_ID);
		} else if (bundle.containsKey(BundleKeys.THREAD_PAGE)) {
			threadPage = bundle.getInt(BundleKeys.THREAD_PAGE);
			lastReadPostId = NON_EXISTANT;
		} else {
			threadPage = 1;
			lastReadPostId = NON_EXISTANT;
		}

		posts = new LinkedList<Posts>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		scrollView = (MyScrollView) this.findViewById(R.id.scrollView);
		scrollView.attachScrollable(this);
		scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		new Load().execute();
	}

	private void populate() {
		threadPage = thread.getResponse().getCurrentPage().intValue();
		setActionBarTitle(thread.getResponse().getThreadTitle() + ", " + threadPage + "/"
				+ thread.getResponse().getPages().intValue());

		if (thread.getResponse().getPosts() != null) {

			for (int i = 0; i < thread.getResponse().getPosts().size(); i++) {
				LinearLayout post_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.thread_post, null);
				posts.add(thread.getResponse().getPosts().get(i));

				TextView author = (TextView) post_layout.findViewById(R.id.author);
				author.setText(posts.getLast().getAuthor().getAuthorName());
				author.setId(posts.getLast().getAuthor().getAuthorId().intValue());
				author.setTag(USER_TAG);
				author.setOnClickListener(this);

				TextView date = (TextView) post_layout.findViewById(R.id.date);
				date.setText(posts.getLast().getAddedTime());

				TextView body = (TextView) post_layout.findViewById(R.id.body);
				body.setText(Html.fromHtml(posts.getLast().getBody(), new AsyncImageGetter(body, this), null));
				Linkify.addLinks(body, Linkify.ALL);

				ImageView reply = (ImageView) post_layout.findViewById(R.id.replyIcon);
				reply.setTag(REPLY_TAG);
				reply.setId(threadId);
				reply.setOnClickListener(this);

				ImageView quote = (ImageView) post_layout.findViewById(R.id.quoteIcon);
				quote.setTag(QUOTE_TAG);
				quote.setId(posts.size() - 1);
				quote.setOnClickListener(this);

				ImageView user = (ImageView) post_layout.findViewById(R.id.userIcon);
				user.setTag(USER_TAG);
				user.setId(posts.getLast().getAuthor().getAuthorId().intValue());
				user.setOnClickListener(this);

				scrollLayout.addView(post_layout);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrolledToBottom() {
		nextPage();
	}

	/**
	 * Load the next page while currentPage < totalPages.
	 */
	private void nextPage() {
		if (isLoaded) {
			if (threadPage < thread.getLastPage()) {
				threadPage++;
				new Load(true).execute();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REPLY_TAG:
				QuoteBuffer.add(threadId, Html.fromHtml(posts.get(v.getId()).getBody()).toString());
				reply();
				break;
			case QUOTE_TAG:
				QuoteBuffer.add(threadId, Html.fromHtml(posts.get(v.getId()).getBody()).toString());
				Toast.makeText(this, "Quoted", Toast.LENGTH_SHORT).show();
				break;
			case USER_TAG:
				openUser(v.getId());
				break;
			default:
				break;
		}
	}

	private void reply() {
		Intent intent = new Intent(this, ReplyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.REPLY_TYPE, BundleKeys.REPLY_TYPE_THREAD);
		bundle.putInt(BundleKeys.THREAD_ID, threadId);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void openUser(int id) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void jumpToPage() {
		new JumpToPageDialog(this, thread.getResponse().getPages()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(ThreadActivity.this, ThreadActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.THREAD_ID, threadId);
					bundle.putInt(BundleKeys.THREAD_PAGE, getPage());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.inbox_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reply_item:
				reply();
				break;
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.refresh_item:
				refresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			super();
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(ThreadActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
				dialog = new ProgressDialog(ThreadActivity.this);
				dialog.setIndeterminate(true);
				dialog.setMessage("Loading...");
				dialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (useEmbeddedDialog) {
				thread = api.forum.thread.Thread.threadFromIdAndPostId(threadId, threadPage);
			} else {
				if (lastReadPostId != NON_EXISTANT) {
					thread = api.forum.thread.Thread.threadFromIdAndPostId(threadId, lastReadPostId);
				} else {
					thread = api.forum.thread.Thread.threadFromIdAndPostId(threadId, threadPage);
				}
			}
			return thread.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			isLoaded = true;
			if (useEmbeddedDialog) {
				hideProgressBar();
			} else {
				dialog.dismiss();
				unlockScreenRotation();
			}

			if (status) {
				populate();
			} else {
				ErrorToast.show(ThreadActivity.this, ThreadActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
