package what.whatandroid.announcements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import api.announcements.BlogPost;
import what.whatandroid.R;

import java.util.List;

/**
 * List adapter for the BlogPosts fragment
 */
public class BlogPostsAdapter extends ArrayAdapter<BlogPost> implements View.OnClickListener {
	private final LayoutInflater inflater;
	private final int resource;
	private List<BlogPost> posts;
	/**
	 * The announcement activity interface so we can select a blog post to show
	 * a detail view of
	 */
	private AnnouncementManager manager;

	/**
	 * Construct the adapter and assign the list of blog posts to view
	 * @param context application context for the adapter
	 * @param resource the view to inflate
	 * @param objects the objects to display
	 */
	public BlogPostsAdapter(Context context, int resource, List<BlogPost> objects) {
		super(context, resource, objects);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		posts = objects;
		try {
			manager = (AnnouncementManager)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement AnnouncementManager");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Recycle views if we can
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		//We need to setup a new view
		else {
			convertView = inflater.inflate(resource, parent, false);
			convertView.setOnClickListener(this);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.announcement_title);
			holder.date = (TextView)convertView.findViewById(R.id.announcement_date);
			holder.snippet = (TextView)convertView.findViewById(R.id.announcement_snippet);
			convertView.setTag(holder);
		}
		holder.post = posts.get(position);
		holder.title.setText(holder.post.getTitle());
		holder.date.setText("By: " + holder.post.getAuthor() + " on: " + holder.post.getBlogTime());
		holder.snippet.setText(holder.post.getBody());
		return convertView;
	}

	/**
	 * When a blog post is clicked we want to show a detail view of it with the full post text
	 */
	@Override
	public void onClick(View v) {
		ViewHolder holder = (ViewHolder)v.getTag();
		manager.showBlogPost(holder.post);
	}

	/**
	 * View holder for blog posts for quicker lookups of views
	 */
	private static class ViewHolder {
		public TextView title, date, snippet;
		public BlogPost post;
	}
}
