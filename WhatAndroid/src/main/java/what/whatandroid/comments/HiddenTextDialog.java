package what.whatandroid.comments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

/**
 * Displays hidden text
 */
public class HiddenTextDialog extends DialogFragment {
	public static final String TITLE = "what.whatandroid.HIDDEN_TITLE",
		TEXT = "what.whatandroid.HIDDEN_TEXT";
	private String title, text;

	/**
	 * Use this factor method to create a hidden text dialog with some title displaying the desired text
	 */
	public static HiddenTextDialog newInstance(String title, String text){
		HiddenTextDialog dialog = new HiddenTextDialog();
		Bundle args = new Bundle();
		args.putString(TITLE, title);
		args.putString(TEXT, text);
		dialog.setArguments(args);
		return dialog;
	}

	public HiddenTextDialog(){
		//Required empty ctor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		title = getArguments().getString(TITLE);
		text = getArguments().getString(TEXT);

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog));
		builder.setTitle(title)
			.setMessage(WhatBBParser.parsebb(text))
			.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
		return builder.create();
	}
}
