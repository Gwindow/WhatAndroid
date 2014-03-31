package what.whatandroid.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
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
	 * Request we're deciding to vote on
	 */
	private Request request;
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

	private final float taxPercent;
	private static final long megaByte = 1048576, gigaByte = 1073741824;

	public VoteDialog(Request r){
		super();
		request = r;
		taxPercent = request.getResponse().getRequestTax().floatValue();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
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
		warning.setText("Minumum vote: " + Utils.toHumanReadableSize(request.getResponse().getMinimumVote().longValue()));

		builder.setView(view)
			.setTitle("Select Vote Amount")
			.setPositiveButton(R.string.vote, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					long unitsAmt = units.getSelectedItemPosition() == 0 ? megaByte : gigaByte;
					float voteAmt = Float.parseFloat(size.getText().toString());
					listener.addBounty(request.getResponse().getRequestId().intValue(), (long)(voteAmt * unitsAmt));
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					VoteDialog.this.getDialog().cancel();
				}
			});
		dialog = builder.create();
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
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			warning.setVisibility(View.VISIBLE);
			warning.setText("Minumum vote: " + Utils.toHumanReadableSize(request.getResponse().getMinimumVote().longValue()));
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

		if (voteBytes < request.getResponse().getMinimumVote().longValue() || newUpload < 0){
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			warning.setVisibility(View.VISIBLE);
			if (voteBytes < request.getResponse().getMinimumVote().longValue()){
				warning.setText("Minumum vote: " + Utils.toHumanReadableSize(request.getResponse().getMinimumVote().longValue()));
			}
			else {
				warning.setText("Cannot afford that vote");
			}
			initText();
		}
		else {
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
			afterTax.setText(Utils.toHumanReadableSize((long)(voteBytes - voteBytes * taxPercent)));
			upload.setText(Utils.toHumanReadableSize(newUpload));
			setRatio(newRatio);
			//Also warn the user about voting themselves into ratio watch but don't prevent it
			if (newRatio < requiredRatio){
				warning.setVisibility(View.VISIBLE);
				warning.setText("Vote may put you on ratio watch");
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
