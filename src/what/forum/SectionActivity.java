package what.forum;

import java.util.ArrayList;
import java.util.List;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.section.Section;
import api.forum.section.Threads;

//TODO reenable author names at some point?
public class SectionActivity extends MyActivity implements OnClickListener, OnLongClickListener {
	private static final String JUMP_UP_STRING = "Up";
	private static final String JUMP_DOWN_STRING = "Down";

	private ScrollView scrollView;
	private LinearLayout scrollLayout;
	private int counter;
	private ProgressDialog dialog;
	private ArrayList<TextView> threadList;
	private TextView sectionTitle;
	private Intent intent;
	private Section section;
	private int id, page;
	private Button backButton, nextButton;
	private Button jumpButton;
	// false is down, true is up
	private boolean isJumped;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.threads, true);
	}

	@Override
	public void init() {
		threadList = new ArrayList<TextView>();
	}

	@Override
	public void load() {
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);
		jumpButton = (Button) this.findViewById(R.id.jumpButton);
		jumpButton.setOnLongClickListener(this);
		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
		sectionTitle = (TextView) findViewById(R.id.titleText);
	}

	@Override
	public void prepare() {
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
		startActivity(intent);
	}

	private void openAuthor(int i) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.user.UserProfilePopUpActivity.class);
		b.putInt("userId", section.getResponse().getThreads().get(i).getAuthorId().intValue());
		intent.putExtras(b);
		startActivity(intent);
	}

	public void back(View v) {
		if (section.hasPreviousPage()) {
			Bundle b = new Bundle();
			intent = new Intent(SectionActivity.this, what.forum.SectionActivity.class);
			b.putInt("id", id);
			b.putInt("page", page - 1);
			intent.putExtras(b);
			startActivity(intent);
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
			startActivity(intent);
		}
	}

	public void newThread(View v) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.NewThreadActivity.class);
		b.putInt("sectionId", Section.getId());
		intent.putExtras(b);
		startActivity(intent);
	}

	public void jump(View v) {
		int dy = scrollView.getHeight();
		int y = scrollView.getScrollY() + dy;
		scrollView.scrollBy(0, dy);
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == jumpButton.getId()) {
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		}
		return false;
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
	public void onRightGesturePerformed() {
		next(null);
	}

	@Override
	public void onDownGesturePerformed() {
		jumpButton.setText(JUMP_UP_STRING);
		isJumped = true;
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public void onUpGesturePerformed() {
		scrollView.fullScroll(ScrollView.FOCUS_UP);
		isJumped = false;
		scrollView.fullScroll(ScrollView.FOCUS_UP);

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
