package what.forum;

import java.util.List;

import what.gui.ActivityNames;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.forum.forumsections.Categories;
import api.forum.forumsections.ForumSections;
import api.soup.MySoup;

import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class ForumActivity extends MyActivity2 implements OnClickListener {
	private static final int SECTION_TAG = 0;
	private LinearLayout scrollLayout;
	private ForumSections forumSections;
	private MyScrollView scrollView;

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
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
		setActionBarTitle("Forum");

		List<Categories> categories = forumSections.getResponse().getCategories();

		for (int i = 0; i < categories.size(); i++) {
			TextView category_title = new TextView(this);
			category_title.setTextAppearance(this, R.style.ForumCategory);
			category_title.setText(categories.get(i).getCategoryName());
			scrollLayout.addView(category_title);
			for (int j = 0; j < categories.get(i).getForums().size(); j++) {
				TextView section_title = new TextView(this);
				section_title.setTextAppearance(this, R.style.ForumSection);
				section_title.setText("\t" + categories.get(i).getForums().get(j).getForumName());
				section_title.setTag(SECTION_TAG);
				section_title.setId(categories.get(i).getForums().get(j).getForumId().intValue());
				section_title.setOnClickListener(this);
				scrollLayout.addView(section_title);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {
		if (Integer.valueOf(v.getTag().toString()) == SECTION_TAG) {
			openSection(v.getId());
		}
	}

	private void openSection(int id) {
		Intent intent = new Intent(this, SectionActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("sectionId", id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		public Load() {
			attachCancelable(this);
		}

		@Override
		public void cancel() {
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean toReturn;
			try {
				forumSections = MySoup.loadForumSections();
				toReturn = forumSections.getStatus();
			} catch (Exception e) {
				e.printStackTrace();
				toReturn = false;
			}
			return toReturn;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			hideIndeterminateProgress();

			unlockScreenRotation();
			if (status) {
				populate();
			}
			if (!status) {
				ErrorToast.show(ForumActivity.this, ForumActivity.class);
			}
		}

	}

}
