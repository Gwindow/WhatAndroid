package what.whatandroid.forums.poll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import api.forum.thread.Answer;
import api.forum.thread.Poll;
import what.whatandroid.R;

/**
 * Adapter that displays a list of the poll answers
 */
public class AnswerAdapter extends ArrayAdapter<Answer> {
	private final LayoutInflater inflater;
	private final DecimalFormat percentFormat;
	private Poll poll;

	public AnswerAdapter(Context context, Poll poll){
		super(context, R.layout.list_poll_answer);
		addAll(poll.getAnswers());
		notifyDataSetChanged();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		percentFormat = new DecimalFormat("#.##");
		this.poll = poll;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_poll_answer, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.voteBar = (ProgressBar) convertView.findViewById(R.id.vote_bar);
			convertView.setTag(holder);
		}
		Answer answer = getItem(position);
		if (poll.hasVoted()){
			float percent = answer.getPercent().floatValue() * 100;
			holder.title.setText(answer.getAnswer() + " - " + percentFormat.format(percent) + "%");
			holder.voteBar.setVisibility(View.VISIBLE);
			holder.voteBar.setProgress((int) (percent));
			//De-activate any selected list item that we voted on
			convertView.setActivated(false);
		}
		else {
			holder.title.setText(answer.getAnswer());
			holder.voteBar.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * Update the displayed vote counts by voting on a some answer
	 *
	 * @param answer answer to vote on, 0 = blank vote, actual answers
	 *               begin at 1
	 */
	public void updateVotes(int answer){
		poll.applyVote(answer);
		notifyDataSetChanged();
	}

	private static class ViewHolder {
		public TextView title;
		public ProgressBar voteBar;
	}
}
