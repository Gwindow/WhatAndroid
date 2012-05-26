package what.forum.section;

import java.util.List;

import what.gui.ActivityNames;
import what.gui.ErrorToast;
import what.gui.JumpToPageDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.gui.ViewSlider;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.section.Section;
import api.forum.section.Threads;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class SectionActivity2 extends MyActivity2 implements Scrollable, OnClickListener {
	private static final int THREAD_TAG = 0;
	private static final int AUTHOR_TAG = 1;
	private static final int LAST_POSTER_TAG = 2;

	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private Section section;
	private int sectionId;
	private int sectionPage = 1;

	private boolean isLoaded;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.FORUM);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.section, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		sectionId = bundle.getInt("sectionId");
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
		new LoadSection().execute();
	}

	/**
	 * Populate section with threads.
	 */
	private void populate() {
		getSupportActionBar().setTitle(
				section.getResponse().getForumName() + ", " + sectionPage + "/" + section.getResponse().getPages());

		List<Threads> threads = section.getResponse().getThreads();
		if (threads != null) {
			for (int i = 0; i < threads.size(); i++) {
				// int background_color = i % 2 == 0 ? R.drawable.color_transparent_white :
				// R.drawable.color_transparent_light_gray;
				ViewSlider thread_layout = (ViewSlider) getLayoutInflater().inflate(R.layout.section_thread, null);
				// thread_layout.setBackgroundResource(background_color);
				TextView thread_title = (TextView) thread_layout.findViewById(R.id.threadTitle);
				thread_title.setText(threads.get(i).getTitle());
				thread_title.setId(threads.get(i).getTopicId().intValue());
				thread_title.setTag(THREAD_TAG);
				thread_title.setOnClickListener(this);
				TextView thread_last_poster = (TextView) thread_layout.findViewById(R.id.threadLastPoster);
				thread_last_poster.setText("Last Poster: " + threads.get(i).getLastAuthorName());
				thread_last_poster.setId(threads.get(i).getLastAuthorId().intValue());
				thread_last_poster.setTag(LAST_POSTER_TAG);

				TextView thread_author = (TextView) thread_layout.findViewById(R.id.threadAuthor);
				thread_author.setText("Author: " + threads.get(i).getAuthorName());
				thread_author.setId(threads.get(i).getAuthorId().intValue());
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
				new LoadSection(true).execute();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
			case THREAD_TAG:
				Toast.makeText(this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
				break;
			case AUTHOR_TAG:
				// TODO fill out
				break;
			case LAST_POSTER_TAG:
				// TODO fill out
				break;
			default:
				break;
		}
	}

	private void jumpToPage() {
		new JumpToPageDialog(this, section.getResponse().getPages().intValue()) {
			@Override
			public void jumpToPage() {
				if (getPage() != -1) {
					Intent intent = new Intent(SectionActivity2.this, SectionActivity2.class);
					Bundle bundle = new Bundle();
					bundle.putInt("sectionPage", getPage());
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

	private class LoadSection extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public LoadSection() {
			super();
		}

		public LoadSection(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(SectionActivity2.this);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				lockScreenRotation();
				dialog = new ProgressDialog(SectionActivity2.this);
				dialog.setIndeterminate(true);
				dialog.setMessage("Loading...");
				dialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			section = Section.sectionFromIdAndPage(sectionId, sectionPage);
			return section.getStatus();
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
			}
			if (!status) {
				ErrorToast.show(SectionActivity2.this, SectionActivity2.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}

}
