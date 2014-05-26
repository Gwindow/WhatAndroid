package what.whatandroid.forums.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.forum.forum.ForumThread;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;

/**
 * Adapter for showing a listing of forum threads
 */
public class ForumListAdapter extends ArrayAdapter<ForumThread> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private ViewForumCallbacks viewForum;

	public ForumListAdapter(Context context){
		super(context, R.layout.list_torrent_file);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			viewForum = (ViewForumCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewForumCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_torrent_file, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		}
		ForumThread thread = getItem(position);
		holder.name.setText(thread.getTitle());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		System.out.println("Position " + position + " clicked");
	}

	private static class ViewHolder {
		public TextView name;
	}
}
