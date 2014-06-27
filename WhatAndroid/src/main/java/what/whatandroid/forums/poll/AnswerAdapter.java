package what.whatandroid.forums.poll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.forum.thread.Answer;
import what.whatandroid.R;

import java.util.List;

/**
 * Adapter that displays a list of the poll answers
 */
public class AnswerAdapter extends ArrayAdapter<Answer> {
	private final LayoutInflater inflater;

	public AnswerAdapter(Context context, List<Answer> answers){
		super(context, R.layout.list_poll_answer);
		addAll(answers);
		notifyDataSetChanged();
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView.setTag(holder);
		}
		Answer answer = getItem(position);
		holder.title.setText(answer.getAnswer());
		return convertView;
	}

	private static class ViewHolder {
		public TextView title;
	}
}
