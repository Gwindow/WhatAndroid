package what.forum;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import api.forum.thread.ForumThread;
import api.forum.thread.Post;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import what.gui.*;
import what.settings.Settings;
import what.user.UserActivity;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Gwindow
 * @since Jun 8, 2012 1:08:36 AM
 */
public class ThreadActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int REPLY_TAG = 0;
	private static final int QUOTE_TAG = 1;
	private static final int USER_TAG = 2;
	private static final int SUBSCRIBE_ITEM_ID = 3;
	private static final int NON_EXISTANT = -1;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private ForumThread thread;
	private int threadPage;
	private int threadId;
	private int lastReadPostId;
	private boolean isLoaded;

	private LinkedList<Post> posts;
	private HashMap<Integer, ImageView> avatarMap;
	private boolean isSubscribed;
	private boolean isSpoilerAlertShown;
	private MyImageLoader imageLoader;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.FORUM);
		super.requestIndeterminateProgress();
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

		posts = new LinkedList<Post>();
		avatarMap = new HashMap<Integer, ImageView>();
		imageLoader = new MyImageLoader(this, R.drawable.dne);
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
		isSubscribed = thread.getResponse().isSubscribed();
		invalidateOptionsMenu();
		threadPage = thread.getResponse().getCurrentPage().intValue();
		setActionBarTitle(thread.getResponse().getThreadTitle() + ", " + threadPage + "/" + thread.getResponse().getPages().intValue());
		if (!isSpoilerAlertShown) {
			spoilerAlertDialog();
		}
		new InstructionDialog(this, InstructionDialog.FORUM);

		if (thread.getResponse().getPosts() != null) {
            for (Post post : thread.getResponse().getPosts()){
                LinearLayout post_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.thread_post, null);
                posts.add(post);

                ImageView avatar = (ImageView) post_layout.findViewById(R.id.avatar);
                if (Settings.getAvatarsEnabled()) {
                    avatarMap.put(post.getPostId().intValue(), avatar);
                    imageLoader.displayImage(post.getAuthor().getAvatar(), avatar);
                } else {
                    avatar.setVisibility(View.GONE);
                }

                TextView author = (TextView) post_layout.findViewById(R.id.author);
                author.setText(post.getAuthor().getAuthorName());
                author.setId(post.getAuthor().getAuthorId().intValue());
                author.setTag(USER_TAG);
                author.setOnClickListener(this);

                TextView date = (TextView) post_layout.findViewById(R.id.date);
                date.setText(post.getAddedTime());

                MyTextView body = (MyTextView) post_layout.findViewById(R.id.body);
                body.setText(post.getBody());
                // Linkify.addLinks(body, Linkify.WEB_URLS);

                ImageView reply = (ImageView) post_layout.findViewById(R.id.replyIcon);
                reply.setTag(REPLY_TAG);
                reply.setId(posts.size() - 1);
                reply.setOnClickListener(this);

                ImageView quote = (ImageView) post_layout.findViewById(R.id.quoteIcon);
                quote.setTag(QUOTE_TAG);
                quote.setId(posts.size() - 1);
                quote.setOnClickListener(this);

                ImageView user = (ImageView) post_layout.findViewById(R.id.userIcon);
                user.setTag(USER_TAG);
                user.setId(post.getAuthor().getAuthorId().intValue());
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

	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case REPLY_TAG:
				QuoteBuffer.add(threadId, posts.get(v.getId()).getQuotableBody());
				reply();
				break;
			case QUOTE_TAG:
				QuoteBuffer.add(threadId, posts.get(v.getId()).getQuotableBody());
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

	// TODO investigate why page jumping doenst work
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
		inflater.inflate(R.menu.thread_menu, menu);

		if (thread != null) {
			String title;
			if (isSubscribed) {
				title = "Unsubscribe";
			} else {
				title = "Subscribe";
			}
			menu.addSubMenu(Menu.NONE, SUBSCRIBE_ITEM_ID, Menu.FIRST, title);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reply_item:
				closeOptionsMenu();
				reply();
				break;
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.refresh_item:
				refresh();
				break;
			case SUBSCRIBE_ITEM_ID:
				if (isSubscribed) {
					thread.unsubscribe();
					Toast.makeText(this, "Unsubscribed", Toast.LENGTH_SHORT).show();
					isSubscribed = false;
				} else {
					thread.subscribe();
					Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
					isSubscribed = true;
				}
				super.invalidateOptionsMenu();
				break;
		}
		if (item.getItemId() == android.R.id.home) {
			return homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			this(false);
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			Log.d("cancel", "cancelled thread");
			super.cancel(true);
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
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (useEmbeddedDialog) {

				thread = ForumThread.fromIdAndPage(threadId, threadPage);
			} else {
				if (lastReadPostId != NON_EXISTANT) {
					thread = ForumThread.fromIdAndPostId(threadId, lastReadPostId);
				} else {
					thread = ForumThread.fromIdAndPage(threadId, threadPage);
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
				hideIndeterminateProgress();
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

	private void spoilerAlertDialog() {
		if (thread.getResponse().getThreadTitle().toLowerCase().contains("spoiler")
				|| thread.getResponse().getThreadId().intValue() == 97258) {
			Toast.makeText(this, "Hide tags do not work and this thread may contain spoilers. Proceed with caution.", Toast.LENGTH_LONG)
					.show();
		}
		isSpoilerAlertShown = true;
	}

}
