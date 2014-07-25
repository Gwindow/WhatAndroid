package what.whatandroid.forums.thread;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import what.whatandroid.R;
import what.whatandroid.comments.WhatBBParser;

/**
 * A dialog fragment where the user can enter their reply to
 * a post or message. Communicates with the activity to request
 * that drafts be saved or discarded or that the current
 * text should be posted to the site.
 */
public class ReplyDialogFragment extends DialogFragment implements View.OnClickListener {
	/**
	 * Listener to receive the users composed message and decisions
	 * about whether to save the draft, post it or discard it
	 */
	public interface ReplyDialogListener {
		/**
		 * Called if we want to post the composed message
		 *
		 * @param message message text to send
		 * @param subject optional subject
		 */
		public void post(String message, String subject);

		/**
		 * Called if the user wants to save the draft of the message
		 *
		 * @param message message draft to save
		 * @param subject optional subject draft to save
		 */
		public void saveDraft(String message, String subject);

		/**
		 * Called if the user wants to discard any saved message
		 * and subject drafts
		 */
		public void discard();
	}

	public static final String DRAFT = "what.whatandroid.replydialogfragment.DRAFT",
		SUBJECT = "what.whatandroid.replydialogfragment.SUBJECT",
		PREVIEWING = "what.whatandroid.replydialogfragment.PREVIEWING";

	/**
	 * The input box for the user to enter their post
	 */
	private EditText postText, postSubject;
	/**
	 * Preview rendering textview and parser to apply styling
	 */
	private TextView preview;
	private Button renderPreview;
	private WhatBBParser whatBBParser = new WhatBBParser();
	/**
	 * Track if we should save the draft since it's done automatically
	 * in onPause but sometimes we wouldn't want to do it, eg. when
	 * discarding or posting the draft
	 */
	private boolean saveDraft = true;
	/**
	 * Listener to alert about changes made to the draft
	 */
	private ReplyDialogListener listener;

	/**
	 * Create a new reply dialog fragment displaying the
	 * user's previous draft to continue editing
	 *
	 * @param draft saved draft of a reply we're resuming editing
	 */
	public static ReplyDialogFragment newInstance(String draft){
		ReplyDialogFragment f = new ReplyDialogFragment();
		Bundle args = new Bundle();
		args.putString(DRAFT, draft);
		f.setArguments(args);
		return f;
	}

	/**
	 * Create a new reply dialog fragment displaying the
	 * user's previous draft to continue editing
	 *
	 * @param draft   saved draft of a message we're resuming editing
	 * @param subject saved subject of a message we're resuming editing
	 */
	public static ReplyDialogFragment newInstance(String draft, String subject){
		ReplyDialogFragment f = new ReplyDialogFragment();
		Bundle args = new Bundle();
		args.putString(DRAFT, draft);
		args.putString(SUBJECT, subject);
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

		try {
			listener = (ReplyDialogListener)getTargetFragment();
		}
		catch (ClassCastException e){
			throw new ClassCastException(getTargetFragment().toString() + " must implement ReplyDialogListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dialog_reply, container, false);
		postText = (EditText)view.findViewById(R.id.post_text);
		postSubject = (EditText)view.findViewById(R.id.subject);
		preview = (TextView)view.findViewById(R.id.post_preview);
		Button discard = (Button)view.findViewById(R.id.discard);
		Button reply = (Button)view.findViewById(R.id.reply);
		renderPreview = (Button)view.findViewById(R.id.render_preview);
		preview.setOnClickListener(this);
		discard.setOnClickListener(this);
		reply.setOnClickListener(this);
		renderPreview.setOnClickListener(this);
		getDialog().setTitle("Compose Reply");

		//If we're restoring from a saved state the edit text takes care of restoring the contents
		//Otherwise we can restore a previous draft to show
		if (savedInstanceState == null){
			if (getArguments().getString(DRAFT) != null){
				postText.append(getArguments().getString(DRAFT));
			}
			if (getArguments().getString(SUBJECT) != null){
				postSubject.append(getArguments().getString(SUBJECT));
				postSubject.setVisibility(View.VISIBLE);
			}
		}
		if (savedInstanceState != null){
			postText.setText(savedInstanceState.getString(DRAFT));
			setPreviewState(savedInstanceState.getBoolean(PREVIEWING));
			if (savedInstanceState.getString(SUBJECT) != null){
				postSubject.append(getArguments().getString(SUBJECT));
				postSubject.setVisibility(View.VISIBLE);
			}
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean(PREVIEWING, preview.isShown());
		outState.putString(DRAFT, postText.getText().toString());
		if (postSubject.isShown()){
			outState.putString(SUBJECT, postSubject.getText().toString());
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		String draft = postText.getText().toString();
		if (saveDraft && !draft.isEmpty()){
			listener.saveDraft(draft, postSubject.getText().toString());
			Toast.makeText(getActivity(), "Draft saved", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v){
		switch (v.getId()){
			//If we click the rendered preview we want to go back to editing
			//to mirror the site behavior
			case R.id.post_preview:
				setPreviewState(false);
				break;
			case R.id.discard:
				saveDraft = false;
				listener.discard();
				getDialog().dismiss();
				break;
			case R.id.reply:
				postReply();
				break;
			//We want to toggle the preview state here, eg. show preview if we aren't
			//and show the edit box if we are
			case R.id.render_preview:
				setPreviewState(preview.getVisibility() != View.VISIBLE);
				break;
			default:
				break;
		}
	}

	/**
	 * Validate that a subject (if being shown) and body text have been entered
	 * before sending a reply
	 */
	private void postReply(){
		if (postSubject.isShown() && postSubject.getText().length() == 0){
			Toast.makeText(getActivity(), "Please enter a subject", Toast.LENGTH_SHORT).show();
			postSubject.requestFocus();
		}
		else if (postText.getText().length() == 0){
			Toast.makeText(getActivity(), "Please enter your message", Toast.LENGTH_SHORT).show();
			//Make sure the edit box is showing and focus on it
			setPreviewState(false);
			postText.requestFocus();
		}
		else {
			saveDraft = false;
			listener.post(postText.getText().toString(), postSubject.getText().toString());
			getDialog().dismiss();
		}
	}

	/**
	 * Set the text box/preview state
	 *
	 * @param showPreview true if we want to show the preview, false to show
	 *                    the edit box
	 */
	private void setPreviewState(boolean showPreview){
		if (showPreview){
			postText.setVisibility(View.GONE);
			preview.setVisibility(View.VISIBLE);
			preview.setText(whatBBParser.parsebb(postText.getText().toString()));
			renderPreview.setText("Edit");
		}
		else {
			preview.setVisibility(View.GONE);
			postText.setVisibility(View.VISIBLE);
			renderPreview.setText("Preview");
		}
	}
}
