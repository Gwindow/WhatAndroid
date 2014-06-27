package what.whatandroid.forums.poll;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import api.forum.thread.Poll;
import api.son.MySon;
import what.whatandroid.R;
import what.whatandroid.forums.ForumActivity;

/**
 * A dialog fragment for displaying a poll to vote on and/or
 * view the results of
 */
public class PollDialog extends DialogFragment implements AdapterView.OnItemClickListener {
	private static final String POLL = "what.whatandroid.polldialog.POLL";
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
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
		poll = (Poll) MySon.toObjectFromString(getArguments().getString(POLL), Poll.class);
		thread = getArguments().getInt(ForumActivity.THREAD_ID);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
			android.R.style.Theme_Holo_Dialog));
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_poll, null);
		answers = (ListView) view.findViewById(R.id.poll_answers);
		adapter = new AnswerAdapter(getActivity(), poll);
		answers.setAdapter(adapter);
		answers.setOnItemClickListener(this);
		if (poll.hasVoted()) {
			answers.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
		}
		builder.setView(view)
			.setTitle(poll.getQuestion())
			.setPositiveButton("Blank, show results", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int vote = answers.getCheckedItemPosition();
					vote = vote == -1 ? 0 : vote + 1;
					System.out.println("Voting on item: " + vote);
					listener.makeVote(thread, vote);
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
					adapter.updateVotes(answers.getCheckedItemPosition());
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
		System.out.println("PollDialog start");
		if (poll.hasVoted()) {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		System.out.println("Position " + position + " was selected");
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Vote");
	}
}
