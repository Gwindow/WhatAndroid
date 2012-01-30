package what.forum;

import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import api.forum.thread.Thread;

public class PostOptionsActivity extends MyActivity {
	private Intent intent;
	private String post;
	private int userId;
	private int threadId;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postoptions);

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		getBundle();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getBundle() {
		Bundle b = this.getIntent().getExtras();
		userId = b.getInt("userId");
		threadId = b.getInt("threadId");
		post = b.getString("post");
	}

	public void quote(View v) {
		QuoteBuffer.add(post);
	}

	public void quoteAndReply(View v) {
		QuoteBuffer.add(post);
		replyDialog();
	}

	public void openUserProfile(View v) {
		Bundle b = new Bundle();
		intent = new Intent(PostOptionsActivity.this, what.user.UserProfilePopUpActivity.class);
		b.putInt("userId", userId);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void reply(View v) {
		replyDialog();
	}

	private void replyDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("Reply");

		final EditText input = new EditText(this);
		input.setGravity(Gravity.TOP);
		input.setGravity(Gravity.LEFT);
		input.setMinHeight(this.getHeight() / 3);
		input.setMinWidth(this.getWidth() / 2);
		input.setText(QuoteBuffer.getBuffer());
		alert.setView(input);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (input.getText().length() > 0) {
					new PostReply().execute(input.getText().toString());
				} else {
					Toast.makeText(PostOptionsActivity.this, "Enter a reply", Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
	}

	private class PostReply extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(PostOptionsActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Thread.postReply(threadId, params[0]);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				Toast.makeText(PostOptionsActivity.this, "Reply posted", Toast.LENGTH_SHORT).show();
			}
			if (status == false) {
				Toast.makeText(PostOptionsActivity.this, "Could not post reply", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
			finish();
		}
	}
}
