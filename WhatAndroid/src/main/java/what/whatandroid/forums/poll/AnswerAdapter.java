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
	private boolean hasVoted;

	public AnswerAdapter(Context context, Poll poll){
		super(context, R.layout.list_poll_answer);
		addAll(poll.getAnswers());
		notifyDataSetChanged();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		percentFormat = new DecimalFormat("#.##");
		hasVoted = poll.hasVoted();
		this.poll = poll;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_poll_answer, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.voteBar = (ProgressBar)convertView.findViewById(R.id.vote_bar);
			convertView.setTag(holder);
		}
		Answer answer = getItem(position);
		if (hasVoted) {
			float percent = answer.getPercent().floatValue() * 100;
			holder.title.setText(answer.getAnswer() + " - " + percentFormat.format(percent) + "%");
			holder.voteBar.setProgress((int) (percent));
		}
		else {
			holder.title.setText(answer.getAnswer());
			holder.voteBar.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * Update the displayed vote counts by voting on a some answer
	 * @param answer answer to vote on, -1 to indicate blank vote
	 */
	public void updateVotes(int answer){
		hasVoted = true;
		if (answer != -1) {
			int total = poll.getTotalVotes().intValue() + 1;
			for (int i = 0; i < getCount(); ++i) {
				Answer a = getItem(i);
				int votes = (int) (poll.getTotalVotes().intValue() * a.getPercent().floatValue());
				if (i == answer) {
					++votes;
				}
				System.out.println("new votes for " + a.getAnswer() + " = " + votes);
				getItem(i).setVotes(votes, total);
			}
		}
		notifyDataSetChanged();
	}

	private static class ViewHolder {
		public TextView title;
		public ProgressBar voteBar;
	}
}
