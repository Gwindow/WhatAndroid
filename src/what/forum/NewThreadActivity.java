package what.forum;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import api.forum.section.Section;

/**
 * @author Gwindow
 * @since Jun 8, 2012 4:59:12 PM
 */
public class NewThreadActivity extends MyActivity2 {
	private EditText title;
	private EditText body;
	private int sectionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Dialog);
		super.onCreate(savedInstanceState);
		enableFade();
		super.setContentView(R.layout.new_thread);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bundle bundle = getIntent().getExtras();
		sectionId = bundle.getInt(BundleKeys.SECTION_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		title = (EditText) findViewById(R.id.threadTitle);
		body = (EditText) findViewById(R.id.body);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepare() {
		fade();
		setActionBarTitle("New Thread...");
	}

	public void post(View v) {
		if (title.length() > 0 && body.length() > 0) {
			new Post().execute(title.getText().toString(), body.getText().toString());
			finish();
		} else {
			Toast.makeText(this, "Nothing entered", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		enableFade();
		super.onPause();
	}

	private class Post extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean toReturn = false;
			try {
				Section.createNewThread(sectionId, params[0], params[1]);
				toReturn = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return toReturn;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(NewThreadActivity.this, "Could not create thread", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NewThreadActivity.this, "Thread created", Toast.LENGTH_SHORT).show();

			}
		}

	}

}
