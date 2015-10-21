package what.whatandroid.request;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import api.cli.Utils;
import api.requests.Request;
import api.soup.MySoup;
import what.whatandroid.R;

/**
 * Dialog for selecting amount of bounty to add to some request
 */
public class VoteDialog extends DialogFragment implements TextWatcher, AdapterView.OnItemSelectedListener {
	/**
	 * Receive the amount of bounty the user is adding in bytes
	 */
	public interface VoteDialogListener {
		/**
		 * Add some amount of bounty to the request
		 *
		 * @param request id of request to add bounty to
		 * @param amt     amount of bounty in bytes
		 */
		public void addBounty(int request, long amt);
	}

	/**
	 * Constants for size of megabyte and gigabyte in bytes and the minimum vote (20 MB)
	 */
	private static final long megaByte = 1048576, gigaByte = 1073741824, minVote = 20 * megaByte;
	private static final String REQUEST_TAX = "what.whatandroid.REQUEST_TAX";
	/**
	 * Request we're deciding to vote on and its tax percentage
	 */
	private int requestId = -1;
	private float taxPercent;
	/**
	 * Input text to enter size and spinner to select units
	 */
	private EditText size;
	private Spinner units;
	/**
	 * Views to display the amount after tax, user's upload and ratio if they make the vote
	 */
	private TextView afterTax, upload, ratio, warning;
	/**
	 * The dialog itself, so we can disable the positive button on invalid input
	 */
	private AlertDialog dialog;
	/**
	 * Listener to send an add bounty request too
	 */
	private VoteDialogListener listener;

	/**
	 * Use this factory method to create a vote dialog displaying a bounty vote prompt for
	 * the request
	 */
	public static VoteDialog newInstance(Request r){
		VoteDialog d = new VoteDialog();
		Bundle args = new Bundle();
		args.putInt(RequestActivity.REQUEST_ID, r.getResponse().getRequestId().intValue());
		args.putFloat(REQUEST_TAX, r.getResponse().getRequestTax().floatValue());
		d.setArguments(args);
		return d;
	}

	public VoteDialog(){
		//Required empty ctor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		requestId = getArguments().getInt(RequestActivity.REQUEST_ID);
		taxPercent = getArguments().getFloat(REQUEST_TAX);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_request_vote, null);
		size = (EditText)view.findViewById(R.id.size);
		units = (Spinner)view.findViewById(R.id.units);
		afterTax = (TextView)view.findViewById(R.id.after_tax);
		upload = (TextView)view.findViewById(R.id.new_upload);
		ratio = (TextView)view.findViewById(R.id.new_ratio);
		warning = (TextView)view.findViewById(R.id.warning);

		size.addTextChangedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			R.array.vote_units, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		units.setAdapter(adapter);
		units.setOnItemSelectedListener(this);
		initText();

		builder.setView(view)
			.setTitle("Select Vote Amount")
			.setPositiveButton(R.string.vote, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					long unitsAmt = units.getSelectedItemPosition() == 0 ? megaByte : gigaByte;
					float voteAmt = Float.parseFloat(size.getText().toString());
					listener.addBounty(requestId, (long) (voteAmt * unitsAmt));
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
		dialog = builder.create();
		//Start the view with the site minimum vote of 20 MB
		size.setText("20");
		size.setSelection(2);
		return dialog;
	}

	/**
	 * Listen to changes on the vote amount and update the new upload/ratio fields appropriately
	 */
	@Override
	public void afterTextChanged(Editable s){
		String input = s.toString();
		if (input.isEmpty()){
			initText();
			if (dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null){
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
			warning.setVisibility(View.VISIBLE);
			warning.setText("Minumum vote: " + Utils.toHumanReadableSize(minVote));
		}
		else {
			updateVote(Float.parseFloat(input));
		}
	}

	/**
	 * Listen to changes on the vote size and update the new upload/ratio fields appropriately
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
		String input = size.getText().toString();
		if (!input.isEmpty()){
			updateVote(Float.parseFloat(input));
		}
	}

	/**
	 * Compute the new user stats as a result of this vote and validate that the vote is ok
	 * to make
	 */
	private void updateVote(float voteAmt){
		long unitsAmt = units.getSelectedItemPosition() == 0 ? megaByte : gigaByte;
		long voteBytes = (long)(voteAmt * unitsAmt);
		long newUpload = MySoup.getIndex().getResponse().getUserstats().getUploaded().longValue() - voteBytes;
		float newRatio = newUpload / MySoup.getIndex().getResponse().getUserstats().getDownloaded().floatValue();
		float requiredRatio = MySoup.getIndex().getResponse().getUserstats().getRequiredRatio().floatValue();

		if (voteBytes < minVote || newUpload < 0){
			if (dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null){
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
			warning.setVisibility(View.VISIBLE);
			if (voteBytes < minVote){
				warning.setText("Minumum vote: " + Utils.toHumanReadableSize(minVote));
			}
			else {
				warning.setText("You can't afford that vote");
			}
			initText();
		}
		else {
			if (dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null){
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
			}
			afterTax.setText(Utils.toHumanReadableSize((long)(voteBytes - voteBytes * taxPercent)));
			upload.setText(Utils.toHumanReadableSize(newUpload));
			setRatio(newRatio);
			//Also warn the user about voting themselves into ratio watch but don't prevent it
			if (newRatio < requiredRatio){
				warning.setVisibility(View.VISIBLE);
				warning.setText("Your ratio will fall below your required ratio");
			}
			else {
				warning.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * Initialize the text views to their initial values (ie no vote)
	 */
	private void initText(){
		afterTax.setText("0.00 MB");
		upload.setText(Utils.toHumanReadableSize(MySoup.getIndex().getResponse().getUserstats().getUploaded().longValue()));
		setRatio(MySoup.getIndex().getResponse().getUserstats().getRatio().floatValue());
	}

	/**
	 * Set the updated ratio for the vote. -1 is interpreted as infinity since that's what the API
	 * gives us back for infinite ratio in the index
	 */
	private void setRatio(float userRatio){
		if (userRatio == -1){
			userRatio = Float.POSITIVE_INFINITY;
		}
		ratio.setText(String.format("%.2f", userRatio) + " / "
			+ String.format("%.2f", MySoup.getIndex().getResponse().getUserstats().getRequiredRatio().floatValue()));
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			listener = (VoteDialogListener)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement VoteDialogListener");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after){
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count){
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent){

	}
}
