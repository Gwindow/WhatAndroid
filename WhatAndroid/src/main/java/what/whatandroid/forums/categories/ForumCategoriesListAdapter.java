package what.whatandroid.forums.categories;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import api.forum.categories.Category;
import api.forum.categories.Forum;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;

/**
 * Displays a list of the forum categories and the most recently replied to
 * post in each forum
 */
public class ForumCategoriesListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private final int VIEW_HEADER = 0, VIEW_ITEM = 1;
	/**
	 * List of categories and forums being shown
	 */
	private List<Category> categories;

	/**
	 * Callbacks to open a specific forum to view
	 */
	private ViewForumCallbacks viewForum;
	private final LayoutInflater inflater;

	public ForumCategoriesListAdapter(Context context){
		categories = new ArrayList<Category>();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			viewForum = (ViewForumCallbacks) context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewForumCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		switch (getItemViewType(position)){
			case VIEW_HEADER:
				return getCategoryView(position, convertView, parent);
			default:
				return getForumView(position, convertView, parent);
		}
	}

	private View getCategoryView(int position, View convertView, ViewGroup parent){
		CategoryViewHolder holder;
		if (convertView != null){
			holder = (CategoryViewHolder) convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_forum_category, parent, false);
			holder = new CategoryViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.category);
			convertView.setTag(holder);
		}
		Category category = (Category) getItem(position);
		holder.name.setText(category.getCategoryName());
		return convertView;
	}

	private View getForumView(int position, View convertView, ViewGroup parent){
		ForumViewHolder holder;
		if (convertView != null){
			holder = (ForumViewHolder) convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_forum, parent, false);
			holder = new ForumViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.forum_name);
			holder.posts = (TextView) convertView.findViewById(R.id.posts);
			holder.topics = (TextView) convertView.findViewById(R.id.topics);
			holder.lastThreadName = (TextView) convertView.findViewById(R.id.last_post_thread);
			holder.lastAuthorName = (TextView) convertView.findViewById(R.id.last_post_username);
			holder.lastPostTime = (TextView) convertView.findViewById(R.id.last_post_time);
			convertView.setTag(holder);
		}
		Forum forum = (Forum) getItem(position);
		holder.name.setText(forum.getForumName());
		holder.posts.setText(forum.getNumPosts().toString());
		holder.topics.setText(forum.getNumTopics().toString());
		holder.lastThreadName.setText(forum.getLastTopic());
		holder.lastAuthorName.setText(forum.getLastPostAuthorName());
		Date lastPost = MySoup.parseDate(forum.getLastTime());
		holder.lastPostTime.setText(DateUtils.getRelativeTimeSpanString(lastPost.getTime(),
			new Date().getTime(), 0, DateUtils.FORMAT_ABBREV_ALL));
		return convertView;
	}

	/**
	 * Get the item being shown at the position. Returns Categorys for header views
	 * and Forums for forum views
	 */
	@Override
	public Object getItem(int position){
		int category = 0, section, p = position;
		while (true){
			int listLen = categories.get(category).getForums().size();
			//If we're in the block for the current category
			if (p - 1 - listLen < 0){
				section = p - 1;
				break;
			}
			else {
				++category;
				p -= 1 + listLen;
			}
		}
		return p == 0 ? categories.get(category)
			: categories.get(category).getForums().get(section);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public int getViewTypeCount(){
		return 2;
	}

	@Override
	public int getCount(){
		int count = 0;
		for (Category c : categories){
			count += 1 + c.getForums().size();
		}
		return count;
	}

	/**
	 * Resolve the position into the view type. The first category header begins at 0
	 * and the section continues for its forum list length, then the next category header
	 * begins and so on. We use this information to reconstruct the view type at the position
	 */
	@Override
	public int getItemViewType(int position){
		for (int i = 0; position > 0; ++i){
			position -= 1 + categories.get(i).getForums().size();
		}
		return position == 0 ? VIEW_HEADER : VIEW_ITEM;
	}

	@Override
	public boolean areAllItemsEnabled(){
		return false;
	}

	@Override
	public boolean isEnabled(int position){
		return getItemViewType(position) == VIEW_ITEM;
	}

	public void addAll(Collection<Category> collection){
		categories.addAll(collection);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Forum forum = (Forum) getItem(position);
		viewForum.viewForum(forum.getForumId().intValue());
	}

	private static class CategoryViewHolder {
		public TextView name;
	}

	private static class ForumViewHolder {
		public TextView name, topics, posts, lastThreadName, lastAuthorName,
			lastPostTime;
	}
}
