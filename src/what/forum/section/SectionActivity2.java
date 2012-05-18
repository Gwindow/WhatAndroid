package what.forum.section;

import java.util.LinkedList;
import java.util.List;

import what.gui.ErrorToast;
import what.gui.MyActivity;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.Scrollable;
import what.gui.ViewSlider;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.forum.section.Section;
import api.forum.section.Threads;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class SectionActivity2 extends MyActivity implements Scrollable {
	private MyScrollView scrollView;
	private LinearLayout scrollLayout;

	private Section section;
	private int sectionId;
	private int sectionPage;
	private LinkedList<ViewSlider> threadList;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		super.setContentView(R.layout.section, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		sectionId = 1;
		// sectionId = myBundle.getInt("sectionId");
		sectionPage = 1;

		threadList = new LinkedList<ViewSlider>();
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
		enableGestures(false);
		new LoadSection().execute();
	}

	/**
	 * Populate section with threads.
	 */
	private void populate() {
		getSupportActionBar().setTitle(section.getResponse().getForumName() + ", " + sectionPage);

		List<Threads> threads = section.getResponse().getThreads();
		if (threads != null) {
			for (int i = 0; i < threads.size(); i++) {
				int background_color = i % 2 == 0 ? R.drawable.color_transparent_white : R.drawable.color_transparent_light_gray;
				ViewSlider thread_layout = (ViewSlider) getLayoutInflater().inflate(R.layout.section_thread, null);
				thread_layout.setBackgroundResource(background_color);
				TextView thread_title = (TextView) thread_layout.findViewById(R.id.threadTitle);
				thread_title.setText(threads.get(i).getTitle());
				TextView thread_author = (TextView) thread_layout.findViewById(R.id.threadAuthor);
				thread_author.setText("Author: " + threads.get(i).getAuthorName());
				threadList.add(thread_layout);
				scrollLayout.addView(threadList.getLast());
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
		if (sectionPage < section.getLastPage()) {
			sectionPage++;
			new LoadSection().execute();
		}
	}

	private class LoadSection extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(SectionActivity2.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			section = Section.sectionFromIdAndPage(sectionId, sectionPage);
			return section.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status) {
				populate();
			}
			dialog.dismiss();
			if (!status) {
				ErrorToast.show(SectionActivity2.this, SectionActivity2.class);
			}
			unlockScreenRotation();
		}
	}

}
