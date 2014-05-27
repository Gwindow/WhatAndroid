package what.whatandroid.forums.forum;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.forum.forum.ForumThread;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;

import java.util.Date;

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
			convertView = inflater.inflate(R.layout.list_forum_thread, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.thread_name);
			holder.replies = (TextView)convertView.findViewById(R.id.replies);
			holder.author = (TextView)convertView.findViewById(R.id.author_name);
			holder.lastPostAuthor = (TextView)convertView.findViewById(R.id.last_post_username);
			holder.lastPostTime = (TextView)convertView.findViewById(R.id.last_post_time);
			convertView.setTag(holder);
		}
		ForumThread thread = getItem(position);
		holder.name.setText(thread.getTitle());
		holder.author.setText(thread.getAuthorName());
		holder.lastPostAuthor.setText(thread.getLastAuthorName());
		Date lastPost = MySoup.parseDate(thread.getLastTime());
		holder.lastPostTime.setText(DateUtils.getRelativeTimeSpanString(lastPost.getTime(),
			new Date().getTime(), 0, DateUtils.FORMAT_ABBREV_ALL));

		if (thread.isRead()){
			holder.name.setTextColor(getContext().getResources().getColor(android.R.color.secondary_text_dark));
		}
		else {
			holder.name.setTextColor(getContext().getResources().getColor(android.R.color.primary_text_dark));
		}

		int replies = thread.getPostCount().intValue();
		if (replies == 0){
			holder.replies.setText("No replies");
		}
		else if (replies == 1){
			holder.replies.setText("1 reply");
		}
		else {
			holder.replies.setText(replies + " replies");
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		System.out.println("Position " + position + " clicked");
	}

	private static class ViewHolder {
		public TextView name, replies, author, lastPostAuthor, lastPostTime;
	}
}
