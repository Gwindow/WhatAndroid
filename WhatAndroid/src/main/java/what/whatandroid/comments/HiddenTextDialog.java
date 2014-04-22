package what.whatandroid.comments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import what.whatandroid.R;

/**
 * Displays hidden text
 */
public class HiddenTextDialog extends DialogFragment {
	public static final String TITLE = "what.whatandroid.HIDDEN_TITLE",
		TEXT = "what.whatandroid.HIDDEN_TEXT";

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
		String title = getArguments().getString(TITLE);
		String text = getArguments().getString(TEXT);
		if (title == null || title.isEmpty()){
			title = "Hidden Text";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog));
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_text, null);
		TextView textView = (TextView)view.findViewById(R.id.text);
		textView.setTextColor(getActivity().getResources().getColor(android.R.color.primary_text_dark));
		textView.setText(WhatBBParser.parsebb(text));
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		builder.setTitle(title)
			.setView(view)
			.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
		return builder.create();
	}
}
