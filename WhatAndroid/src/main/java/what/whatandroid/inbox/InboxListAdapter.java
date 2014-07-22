package what.whatandroid.inbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
		super(context, R.layout.list_group);
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
			convertView = inflater.inflate(R.layout.list_group, parent, false);
			holder = new ViewHolder();
			holder.subject = (TextView)convertView.findViewById(R.id.group_category);
			convertView.setTag(holder);
		}
		Message message = getItem(position);
		holder.subject.setText(message.getSubject());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Message m = getItem(position);
		viewConversation.viewConversation(m.getConvId().intValue());
	}

	private static class ViewHolder {
		public TextView subject;
	}
}
