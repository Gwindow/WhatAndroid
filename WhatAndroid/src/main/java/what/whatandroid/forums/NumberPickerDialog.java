package what.whatandroid.forums;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import what.whatandroid.R;

/**
 * Dialog for showing users a number picker that they can
 * use to select a number. Eg. in the forums/threads we
 * use this to pick page numbers to jump too
 * <p/>
 * The number selected is passed back through the target fragment's
 * onActivityResultCallback which should implement the NumberPickerListener
 */
public class NumberPickerDialog extends DialogFragment {
	/**
	 * Interface to receive the picked number
	 */
	public interface NumberPickerListener {
		/**
		 * Called when a number is picked
		 *
		 * @param number the chosen number
		 */
		public void pickNumber(int number);
	}

	public static final String MIN = "what.whatandroid.numberpickerdialog.MIN",
		MAX = "what.whatandroid.numberpickerdialog.MAX",
		TITLE = "what.whatandroid.numberpickerdialog.TITLE";

	private NumberPickerListener listener;

	/**
	 * Create a NumberPickerDialog to pick values in the range [min, max]
	 * and with the desired title
	 *
	 * @param title title for the dialog
	 * @param min   min value that should be selectable
	 * @param max   max value that should be selectable
	 */
	public static NumberPickerDialog newInstance(String title, int min, int max){
		NumberPickerDialog d = new NumberPickerDialog();
		Bundle args = new Bundle();
		args.putString(TITLE, title);
		args.putInt(MIN, min);
		args.putInt(MAX, max);
		d.setArguments(args);
		return d;
	}

	public NumberPickerDialog(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try {
			listener = (NumberPickerListener)getTargetFragment();
		}
		catch (ClassCastException e){
			throw new ClassCastException(getTargetFragment().toString() + " must implement NumberPickerListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
			android.R.style.Theme_Holo_Dialog_MinWidth));
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.dialog_number_picker, null);
		final NumberPicker picker = (NumberPicker)view.findViewById(R.id.number_picker);

		Bundle args = getArguments();
		picker.setMinValue(args.getInt(MIN));
		picker.setMaxValue(args.getInt(MAX));

		builder.setView(view)
			.setTitle(args.getString(TITLE))
			.setPositiveButton("Select", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					listener.pickNumber(picker.getValue());
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
		return builder.create();
	}
}
