package what.forum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.forumsections.Categories;
import api.forum.forumsections.ForumSections;
import api.soup.MySoup;

public class ForumSectionsListActivity extends MyActivity implements OnClickListener {
	private ScrollView scrollView;
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private ForumSections forumSections;
	private LinkedList<TextView> sectionList;
	private HashMap<Integer, Integer> idMap;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.sections, true);
	}

	@Override
	public void init() {
		sectionList = new LinkedList<TextView>();
		idMap = new HashMap<Integer, Integer>();
	}

	@Override
	public void load() {
		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
	}

	@Override
	public void prepare() {
		new LoadForumSections().execute();
	}

	private void populateLayout() {
		int counter = 0;
		List<Categories> categories = forumSections.getResponse().getCategories();
		for (int i = 0; i < categories.size(); i++) {
			TextView categoryTitle = ((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
			categoryTitle.setText(categories.get(i).getCategoryName());
			scrollLayout.addView(categoryTitle);
			for (int j = 0; j < categories.get(i).getForums().size(); j++) {
				TextView sectionTitle;
				if ((j % 2) == 0) {
					sectionTitle = (TextView) getLayoutInflater().inflate(R.layout.forum_name_even, null);
				} else {
					sectionTitle = (TextView) getLayoutInflater().inflate(R.layout.forum_name_odd, null);
				}
				sectionTitle.setText(categories.get(i).getForums().get(j).getForumName());
				sectionTitle.setId(counter);
				sectionTitle.setOnClickListener(this);
				sectionList.add(sectionTitle);
				idMap.put(counter, categories.get(i).getForums().get(j).getForumId().intValue());
				counter++;
				scrollLayout.addView(sectionList.getLast());
			}
		}

	}

	private void openSection(int id) {
		Bundle b = new Bundle();
		intent = new Intent(ForumSectionsListActivity.this, what.forum.SectionActivity.class);
		b.putInt("id", id);
		b.putInt("page", 1);
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < sectionList.size(); i++) {
			if (v.getId() == sectionList.get(i).getId()) {
				openSection(idMap.get(v.getId()));
			}
		}
	}

	@Override
	public void onDownGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);

	}

	private class LoadForumSections extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(ForumSectionsListActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			forumSections = MySoup.loadForumSections();
			return forumSections.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(ForumSectionsListActivity.this, "Could not load forum sections", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
