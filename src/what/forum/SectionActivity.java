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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import api.forum.section.Section;
import api.forum.section.Threads;

public class SectionActivity extends MyActivity implements OnClickListener {
	private TableLayout scrollTable;
	private int counter;
	private ProgressDialog dialog;
	private ArrayList<TableRow> threadList = new ArrayList<TableRow>();
	private ArrayList<Button> lastReadList = new ArrayList<Button>();
	private ArrayList<TextView> titleList = new ArrayList<TextView>();
	private ArrayList<TextView> authorList = new ArrayList<TextView>();
	private TextView sectionTitle;
	private Intent intent;
	private Section section;
	private int id, page;
	private Button backButton, nextButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.threads);
		backButton = (Button) this.findViewById(R.id.previousButton);
		nextButton = (Button) this.findViewById(R.id.nextButton);

		scrollTable = (TableLayout) findViewById(R.id.scrollLayout);
		sectionTitle = (TextView) findViewById(R.id.titleText);

		setButtonState(backButton, false);
		setButtonState(nextButton, false);
		getBundle();
		new LoadSection().execute();
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		id = b.getInt("id");
		page = b.getInt("page");

	}

	private void populateLayout() {
		setButtonState(backButton, section.hasPreviousPage());
		setButtonState(nextButton, section.hasNextPage());
		int rowLayoutId;
		int textLayoutId;

		sectionTitle.setText(section.getResponse().getForumName() + ", page " + section.getResponse().getCurrentPage());

		List<Threads> threads = section.getResponse().getThreads();
		for (int i = 0; i < threads.size(); i++) {
			if ((i % 2) == 0) {
				rowLayoutId = R.layout.thread_name_even;
				textLayoutId = R.layout.forum_name_even;
			} else {
				rowLayoutId = R.layout.thread_name_odd;
				textLayoutId = R.layout.forum_name_odd;
			}

			threadList.add((TableRow) getLayoutInflater().inflate(rowLayoutId, null));

			lastReadList.add(new Button(this));
			lastReadList.get(i).setBackgroundResource(R.drawable.right_arrow);
			lastReadList.get(i).setOnClickListener(this);

			titleList.add((TextView) getLayoutInflater().inflate(textLayoutId, null));
			authorList.add((TextView) getLayoutInflater().inflate(textLayoutId, null));
			if (threads.get(i).isLocked()) {
				titleList.get(i).setText("Locked: " + threads.get(i).getTitle());
				authorList.get(i).setText("\t" + threads.get(i).getAuthorName());
			} else if (threads.get(i).isSticky()) {
				titleList.get(i).setText("Sticky: " + threads.get(i).getTitle());
				authorList.get(i).setText("\t" + threads.get(i).getAuthorName());
			} else if (threads.get(i).isLocked() && threads.get(i).isSticky()) {
				titleList.get(i).setText("Locked Sticky: " + threads.get(i).getTitle());
				authorList.get(i).setText("\t" + threads.get(i).getAuthorName());
			} else {
				titleList.get(i).setText(threads.get(i).getTitle());
				authorList.get(i).setText("\t" + threads.get(i).getAuthorName());
			}
			titleList.get(i).setSingleLine(false);
			titleList.get(i).setOnClickListener(this);
			authorList.get(i).setOnClickListener(this);

			threadList.get(i).addView(lastReadList.get(i));
			threadList.get(i).addView(titleList.get(i));
			threadList.get(i).addView(authorList.get(i));

			scrollTable.setColumnShrinkable(0, true);
			scrollTable.setColumnShrinkable(1, true);
			scrollTable.setColumnShrinkable(2, true);
			scrollTable.addView(threadList.get(i), new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			counter++;
		}
		idGenerator();
	}

	private void idGenerator() {
		for (int i = 0; i < counter; i++) {
			titleList.get(i).setId(i);
			lastReadList.get(i).setId(i + counter);
			authorList.get(i).setId(i + (2 * counter));
		}
	}

	private void openLastReadThread(int i) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.ThreadActivity.class);
		b.putInt("id", Integer.valueOf(section.getResponse().getThreads().get(i).getTopicId()));
		b.putInt("page", Integer.valueOf(section.getResponse().getThreads().get(i).getLastReadPage()));
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void openThread(int i) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.ThreadActivity.class);
		b.putInt("id", Integer.valueOf(section.getResponse().getThreads().get(i).getTopicId()));
		b.putInt("page", 1);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	private void openAuthor(int i) {
		Toast.makeText(this, "author " + i, Toast.LENGTH_SHORT).show();
	}

	public void back(View v) {
		finish();
	}

	public void next(View v) {
		Bundle b = new Bundle();
		intent = new Intent(SectionActivity.this, what.forum.SectionActivity.class);
		b.putInt("id", id);
		b.putInt("page", 2);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < (threadList.size()); i++) {
			if (v.getId() == titleList.get(i).getId()) {
				openThread(i);
			}
			if (v.getId() == authorList.get(i).getId()) {
				openAuthor(i);
			}
			if (v.getId() == lastReadList.get(i).getId()) {
				openLastReadThread(i);
			}
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {
				if (section.hasNextPage()) {
					next(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				finish();
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
				Toast.makeText(SectionActivity.this, "Could not load subscriptions", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}
}
