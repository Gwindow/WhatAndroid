package what.forum;

import java.util.ArrayList;
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
import android.widget.TextView;
import android.widget.Toast;
import api.forum.forumsections.ForumSections;
import api.forum.forumsections.Forums;
import api.soup.MySoup;

public class ForumSectionsListActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	ForumSections forumSections;
	private List<Forums> forumsList;
	private ArrayList<TextView> sectionTitleList = new ArrayList<TextView>();
	private ArrayList<TextView> sectionList = new ArrayList<TextView>();
	private LinkedList<TextView> sectionList2 = new LinkedList<TextView>();
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sections);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		new LoadForumSections().execute();
	}

	private void populateLayout() {
		int counter = 0;
		for (int i = 0; i < forumSections.getResponse().getCategories().size(); i++) {
			sectionTitleList.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
			sectionTitleList.get(i).setText(forumSections.getResponse().getCategories().get(i).getCategoryName());
			scrollLayout.addView(sectionTitleList.get(i));
			for (int j = 0; j < forumSections.getResponse().getCategories().get(i).getForums().size(); j++) {
				if ((j % 2) == 0) {
					sectionList2.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_even, null));
				} else {
					sectionList2.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_odd, null));
				}

				sectionList2.get(counter).setText(
						forumSections.getResponse().getCategories().get(i).getForums().get(j).getForumName());
				sectionList2.get(counter).setId(counter);
				sectionList2.get(counter).setOnClickListener(this);
				scrollLayout.addView(sectionList2.get(counter));
				counter++;
			}
		}
	}

	private void populateLayout2() {
		forumSections.loadForumsList();
		forumsList = forumSections.getForumsList();

		// TODO if statement
		for (int i = 0; i < forumSections.getResponse().getCategories().size(); i++) {
			sectionTitleList.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
			sectionTitleList.get(i).setText(forumSections.getResponse().getCategories().get(i).getCategoryName());
			scrollLayout.addView(sectionTitleList.get(i));
			for (int j = 0; j < forumSections.getResponse().getCategories().get(i).getForums().size(); j++) {
				if ((j % 2) == 0) {
					sectionList2.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_even, null));
				} else {
					sectionList2.add((TextView) getLayoutInflater().inflate(R.layout.forum_name_odd, null));
				}
				sectionList2.getLast().setText(
						forumSections.getResponse().getCategories().get(i).getForums().get(j).getForumName());
				sectionList2.getLast().setId(j);
				sectionList2.getLast().setOnClickListener(this);
				scrollLayout.addView(sectionList2.getLast());
			}
		}
	}

	private void openSection(int id) {
		Bundle b = new Bundle();
		intent = new Intent(ForumSectionsListActivity.this, what.forum.SectionActivity.class);
		Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
		b.putInt("id", id);
		b.putInt("page", 1);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < sectionList2.size(); i++) {
			if (v.getId() == sectionList2.get(i).getId()) {
				openSection(forumsList.get(i).getForumId().intValue());
			}
		}
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
				populateLayout2();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(ForumSectionsListActivity.this, "Could not load forum sections", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
