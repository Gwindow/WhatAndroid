package what.whatandroid.forums.thread;

import android.content.Intent;
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
	public static final String DRAFT = "what.whatandroid.replydialogfragment.DRAFT",
		PREVIEWING = "what.whatandroid.replydialogfragment.PREVIEWING";
	public static final int DISCARD = -1, SAVE_DRAFT = 0, POST_REPLY = 1;

	/**
	 * The input box for the user to enter their post
	 */
	private EditText postText;
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
		postText = (EditText)view.findViewById(R.id.post_text);
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
		if (savedInstanceState == null && getArguments().getString(DRAFT) != null){
			postText.append(getArguments().getString(DRAFT));
		}
		if (savedInstanceState != null){
			postText.setText(savedInstanceState.getString(DRAFT));
			setPreviewState(savedInstanceState.getBoolean(PREVIEWING));
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean(PREVIEWING, preview.getVisibility() == View.VISIBLE);
		outState.putString(DRAFT, postText.getText().toString());
	}

	@Override
	public void onPause(){
		super.onPause();
		String draft = postText.getText().toString();
		if (saveDraft && !draft.isEmpty()){
			Intent intent = new Intent();
			intent.putExtra(DRAFT, draft);
			getTargetFragment().onActivityResult(0, SAVE_DRAFT, intent);
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
				getTargetFragment().onActivityResult(0, DISCARD, null);
				getDialog().dismiss();
				break;
			case R.id.reply:
				saveDraft = false;
				Intent intent = new Intent();
				intent.putExtra(DRAFT, postText.getText().toString());
				getTargetFragment().onActivityResult(0, POST_REPLY, intent);
				getDialog().dismiss();
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
