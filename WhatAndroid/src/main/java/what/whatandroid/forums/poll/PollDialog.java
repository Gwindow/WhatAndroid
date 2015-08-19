package what.whatandroid.forums.poll;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import api.forum.thread.Poll;
import api.son.MySon;
import what.whatandroid.R;
import what.whatandroid.forums.ForumActivity;

/**
 * A dialog fragment for displaying a poll to vote on and/or
 * view the results of
 */
public class PollDialog extends DialogFragment implements AdapterView.OnItemClickListener {
	public static final String POLL = "what.whatandroid.polldialog.POLL";
	/**
	 * Listener to alert when we make a vote
	 */
	private PollDialogListener listener;
	/**
	 * The poll being voted on
	 */
	private Poll poll;
	/**
	 * Which thread this poll is part of
	 */
	private int thread;
	/**
	 * List displaying the poll responses, used to find which response is selected
	 */
	private ListView answers;
	/**
	 * Displays the total number of votes in the poll, shown if user has voted
	 */
	private TextView totalVotes;
	/**
	 * Adapter displaying the votes to notify once we make a vote to update
	 */
	private AnswerAdapter adapter;
	/**
	 * The alert dialog being shown
	 */
	private AlertDialog dialog;

	public interface PollDialogListener {
		/**
		 * Request that a vote be made on the poll for the thread
		 * Implementer should run the Poll voting API request
		 *
		 * @param thread thread containing poll to vote on
		 * @param vote   vote to make
		 */
		public void makeVote(int thread, int vote);
	}

	public static PollDialog newInstance(Poll poll, int thread) {
		PollDialog d = new PollDialog();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.THREAD_ID, thread);
		args.putString(POLL, MySon.toJson(poll, Poll.class));
		d.setArguments(args);
		return d;
	}

	public PollDialog() {
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (PollDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement PollDialogListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			poll = (Poll) MySon.toObjectFromString(savedInstanceState.getString(POLL), Poll.class);
		}
		else {
			poll = (Poll) MySon.toObjectFromString(getArguments().getString(POLL), Poll.class);
		}
		thread = getArguments().getInt(ForumActivity.THREAD_ID);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_poll, null);
		answers = (ListView)view.findViewById(R.id.poll_answers);
		totalVotes = (TextView)view.findViewById(R.id.total_votes);
		adapter = new AnswerAdapter(getActivity(), poll);
		answers.setAdapter(adapter);
		answers.setOnItemClickListener(this);

		if (poll.hasVoted()){
			answers.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
			totalVotes.setText("Votes: " + poll.getTotalVotes());
			totalVotes.setVisibility(View.VISIBLE);
		}
		builder.setView(view)
			.setTitle(poll.getQuestion())
			.setPositiveButton("Blank, show results", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//We don't do anything here since we override it later to make clicking vote
					//not close the poll, however we do need to create a positive button to have
					//one show up in the dialog
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		dialog = builder.create();
		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (poll.hasVoted()) {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
		}
		else {
			if (answers.getCheckedItemCount() > 0){
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Vote");
			}
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//Notify the listener we voted and update the shown items in the adapter, updating the poll
					int vote = answers.getCheckedItemPosition();
					vote = vote == -1 ? 0 : vote + 1;
					listener.makeVote(thread, vote);
					adapter.updateVotes(vote);
					answers.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
					dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
					totalVotes.setText("Votes: " + poll.getTotalVotes());
					totalVotes.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(POLL, MySon.toJson(poll, Poll.class));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Vote");
	}
}
