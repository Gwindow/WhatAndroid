package what.forum;

import java.util.List;

import api.forum.section.ForumThread;
import what.gui.ActivityNames;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.gui.ViewSlider;
import what.settings.Settings;
import what.user.UserActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.forum.section.Section;
import api.util.Tuple;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class SectionActivity extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int AUTHOR_TAG = 1;
	private static final int LAST_POSTER_TAG = 2;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private Section section;
	private int sectionId;
	private int sectionPage;

	private boolean isLoaded;

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
		sectionId = bundle.getInt(BundleKeys.SECTION_ID);
		if (bundle.containsKey(BundleKeys.SECTION_PAGE)) {
			sectionPage = bundle.getInt(BundleKeys.SECTION_PAGE);
		} else {
			sectionPage = 1;
		}
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

	/**
	 * Populate section with threads.
	 */
	private void populate() {
		setActionBarTitle(section.getResponse().getForumName() + ", " + sectionPage + "/" + section.getResponse().getPages());

		List<ForumThread> threads = section.getResponse().getThreads();
		if (threads != null) {
            for (ForumThread thread : threads){
				ViewSlider thread_layout = (ViewSlider) getLayoutInflater().inflate(R.layout.section_thread, null);
				TextView thread_title = (TextView) thread_layout.findViewById(R.id.threadTitle);
				String title = thread.getTitle();
				if (thread.isLocked()) {
					title = "[L] " + title;
				}
				if (thread.isSticky()) {
					title = "[S] " + title;
				}
				if (!thread.isRead() && Settings.getBoldSetting()) {
					thread_title.setTypeface(null, Typeface.BOLD);
				}
				thread_title.setText(title);
				thread_title.setTag(new Tuple<Number, Number>(thread.getTopicId(), thread.getLastReadPostId()));
				thread_title.setHorizontallyScrolling(true);
				thread_title.setOnClickListener(this);

				TextView thread_last_poster = (TextView) thread_layout.findViewById(R.id.threadLastPoster);
				thread_last_poster.setText("Last Poster: " + thread.getLastAuthorName());
				thread_last_poster.setId(thread.getLastAuthorId().intValue());
				thread_last_poster.setTag(LAST_POSTER_TAG);

				TextView thread_author = (TextView) thread_layout.findViewById(R.id.threadAuthor);
				thread_author.setText("Author: " + thread.getAuthorName());
				thread_author.setId(thread.getAuthorId().intValue());
				thread_author.setTag(AUTHOR_TAG);

				scrollLayout.addView(thread_layout);
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
			if (sectionPage < section.getLastPage()) {
				sectionPage++;
				new Load(true).execute();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof Tuple<?, ?>) {
			@SuppressWarnings("unchecked")
			Tuple<Number, Number> tuple = ((Tuple<Number, Number>) v.getTag());
			openThread(tuple.getA(), tuple.getB());
		} else {
			switch (Integer.valueOf(v.getTag().toString())) {
				case AUTHOR_TAG:
					openUser(v.getId());
					break;
				case LAST_POSTER_TAG:
					openUser(v.getId());
					break;
				default:
					break;
			}
		}
	}

	private void openThread(Number threadId, Number lastReadPostId) {
		Intent intent = new Intent(this, ThreadActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.THREAD_ID, threadId.intValue());
		if (lastReadPostId != null) {
			bundle.putInt(BundleKeys.THREAD_LAST_READ_POST_ID, lastReadPostId.intValue());

		}
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void newThread() {
		Intent intent = new Intent(this, NewThreadActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.SECTION_ID, sectionId);
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
		new JumpToPageDialog(this, section.getResponse().getPages().intValue()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(SectionActivity.this, SectionActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(BundleKeys.SECTION_ID, sectionId);
					bundle.putInt(BundleKeys.SECTION_PAGE, getPage());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}.create().show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.section_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_thread_item:
				newThread();
				break;
			case R.id.jump_page_item:
				jumpToPage();
				break;
			case R.id.refresh_item:
				refresh();
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
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(SectionActivity.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			section = Section.fromIdAndPage(sectionId, sectionPage);
			return section.getStatus();
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
			}
			if (!status) {
				ErrorToast.show(SectionActivity.this, SectionActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
