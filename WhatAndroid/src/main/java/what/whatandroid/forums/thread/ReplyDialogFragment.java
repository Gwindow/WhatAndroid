package what.whatandroid.forums.thread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import what.whatandroid.R;

/**
 * A dialog fragment where the user can enter their reply to
 * a post or message. Communicates with the activity to request
 * that drafts be saved or discarded or that the current
 * text should be posted to the site.
 */
public class ReplyDialogFragment extends DialogFragment implements View.OnClickListener {
	public static final String DRAFT = "what.whatandroid.replydialogfragment.DRAFT";
	public static final int DISCARD = -1, SAVE_DRAFT = 0, POST_REPLY = 1;

	private EditText postText;
	/**
	 * Track if we should save the draft since it's done automatically
	 * in onPause but sometimes we wouldn't want to do it, eg. when
	 * discarding or posting the draft
	 */
	private boolean saveDraft = true;

	/**
	 * Create a new reply dialog fragment optionally displaying the
	 * user's previous draft to continue editing
	 *
	 * @param draft saved draft of a post we're resuming editing
	 */
	public static ReplyDialogFragment newInstance(String draft){
		ReplyDialogFragment f = new ReplyDialogFragment();
		Bundle args = new Bundle();
		args.putString(DRAFT, draft);
		f.setArguments(args);
		return f;
	}

	public ReplyDialogFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dialog_reply, container, false);
		postText = (EditText) view.findViewById(R.id.post_text);
		Button discard = (Button) view.findViewById(R.id.discard);
		Button reply = (Button) view.findViewById(R.id.reply);
		discard.setOnClickListener(this);
		reply.setOnClickListener(this);
		getDialog().setTitle("Compose Reply");

		//If we're restoring from a saved state the edit text takes care of restoring the contents
		//Otherwise we can restore a previous draft to show
		if (savedInstanceState == null && getArguments().getString(DRAFT) != null){
			postText.append(getArguments().getString(DRAFT));
		}
		return view;
	}

	@Override
	public void onPause(){
		super.onPause();
		if (saveDraft){
			Intent intent = new Intent();
			intent.putExtra(DRAFT, postText.getText().toString());
			getTargetFragment().onActivityResult(0, SAVE_DRAFT, intent);
			System.out.println("Draft saved (dialog)");
			Toast.makeText(getActivity(), "Draft saved", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v){
		saveDraft = false;
		switch (v.getId()){
			case R.id.discard:
				getTargetFragment().onActivityResult(0, DISCARD, null);
				getDialog().dismiss();
				break;
			case R.id.reply:
				Intent intent = new Intent();
				intent.putExtra(DRAFT, postText.getText().toString());
				getTargetFragment().onActivityResult(0, POST_REPLY, intent);
				getDialog().dismiss();
				break;
			default:
				break;
		}
	}
}
