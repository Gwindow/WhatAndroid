package what.forum;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.section.Section;
import api.forum.section.Threads;

//TODO reenable author names at some point?
public class SectionActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private int counter;
	private ProgressDialog dialog;
	private ArrayList<TextView> threadList = new ArrayList<TextView>();
	private TextView sectionTitle;
	private Intent intent;
	private Section section;
	private int id, page;
	private Button backButton, nextButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.threads, true);

		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);

		scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
		sectionTitle = (TextView) findViewById(R.id.titleText);

		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		getBundle();
		new LoadSection().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		try {
			page = b.getInt("page");
		} catch (Exception e) {
			page = 1;
		}

	}

	private void populateLayout() {
		setButtonState(backButton, section.hasPreviousPage());
		setButtonState(nextButton, section.hasNextPage());
		int textLayoutId;

		sectionTitle
				.setText(section.getResponse().getForumName() + ", page " + section.getResponse().getCurrentPage().intValue());

		List<Threads> threads = section.getResponse().getThreads();
		for (int i = 0; i < threads.size(); i++) {
			if ((i % 2) == 0) {
				textLayoutId = R.layout.forum_name_even;
			} else {
				textLayoutId = R.layout.forum_name_odd;
			}

			threadList.add((TextView) getLayoutInflater().inflate(textLayoutId, null));
			if (threads.get(i).isLocked()) {
				threadList.get(i).setText("Locked: " + threads.get(i).getTitle());
			} else if (threads.get(i).isSticky()) {
				threadList.get(i).setText("Sticky: " + threads.get(i).getTitle());
			} else if (threads.get(i).isLocked() && threads.get(i).isSticky()) {
				threadList.get(i).setText("Locked Sticky: " + threads.get(i).getTitle());
			} else {
				threadList.get(i).setText(threads.get(i).getTitle());
			}
			threadList.get(i).setSingleLine(true);
			threadList.get(i).setOnClickListener(this);

			scrollLayout.addView(threadList.get(i));
			counter++;
		}
		idGenerator();
	}

	private void idGenerator() {
		for (int i = 0; i < counter; i++) {
			threadList.get(i).setId(i);
		}
	}

	private void openThread(int i) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.ThreadActivity.class);
		b.putInt("id", (section.getResponse().getThreads().get(i).getTopicId().intValue()));

		// if the thread has been read in the past automatically jump to last read page
		if (section.getResponse().getThreads().get(i).isRead()) {
			b.putInt("page", (section.getResponse().getThreads().get(i).getLastReadPage().intValue()));
		} else {
			b.putInt("page", 1);
		}
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void openAuthor(int i) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.user.UserProfilePopUpActivity.class);
		b.putInt("userId", section.getResponse().getThreads().get(i).getAuthorId().intValue());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void back(View v) {
		if (section.hasPreviousPage()) {
			Bundle b = new Bundle();
			intent = new Intent(SectionActivity.this, what.forum.SectionActivity.class);
			b.putInt("id", id);
			b.putInt("page", page - 1);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		} else {
			finish();
		}
	}

	public void next(View v) {
		if (section.hasNextPage()) {
			Bundle b = new Bundle();
			intent = new Intent(SectionActivity.this, what.forum.SectionActivity.class);
			b.putInt("id", id);
			b.putInt("page", page + 1);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}
	}

	public void newThread(View v) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.NewThreadActivity.class);
		b.putInt("sectionId", Section.getId());
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (threadList.size()); i++) {
			if (v.getId() == threadList.get(i).getId()) {
				openThread(i);
			}
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

	private class LoadSection extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(SectionActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			section = Section.sectionFromIdAndPage(id, page);
			return section.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(SectionActivity.this, "Could not load section", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
