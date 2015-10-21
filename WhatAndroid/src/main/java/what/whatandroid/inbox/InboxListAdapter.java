package what.whatandroid.inbox;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import api.inbox.inbox.Message;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewConversationCallbacks;

/**
 * Adapter for displaying a list of conversations in the inbox
 */
public class InboxListAdapter extends ArrayAdapter<Message> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private ViewConversationCallbacks viewConversation;

	public InboxListAdapter(Context context){
		super(context, R.layout.list_message);
		inflater = LayoutInflater.from(context);
		try {
			viewConversation = (ViewConversationCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewConversationCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_message, parent, false);
			holder = new ViewHolder();
			holder.subject = (TextView)convertView.findViewById(R.id.subject);
			holder.sender = (TextView)convertView.findViewById(R.id.sender);
			holder.date = (TextView)convertView.findViewById(R.id.date);
			holder.sticky = (ImageView)convertView.findViewById(R.id.sticky_thread);
			convertView.setTag(holder);
		}
		Message message = getItem(position);
		holder.subject.setText(message.getSubject());
		holder.sender.setText("Sender: " + message.getUsername());
		holder.date.setText(DateUtils.getRelativeTimeSpanString(message.getDate().getTime(),
			new Date().getTime(), 0, DateUtils.FORMAT_ABBREV_ALL));

		if (message.isUnread()){
			holder.subject.setTextColor(getContext().getResources().getColor(android.R.color.primary_text_dark_nodisable));
		}
		else {
			holder.subject.setTextColor(getContext().getResources().getColor(android.R.color.secondary_text_dark_nodisable));
		}

		if (message.isSticky()){
			holder.sticky.setVisibility(View.VISIBLE);
		}
		else {
			holder.sticky.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Message m = getItem(position);
		m.setUnread(false);
		viewConversation.viewConversation(m.getConvId().intValue());
	}

	private static class ViewHolder {
		public TextView subject, sender, date;
		public ImageView sticky;
	}
}
