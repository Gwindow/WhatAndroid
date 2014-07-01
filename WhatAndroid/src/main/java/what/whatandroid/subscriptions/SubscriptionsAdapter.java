package what.whatandroid.subscriptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import api.subscriptions.ForumThread;
import api.subscriptions.Subscriptions;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;

/**
 * Adapter for displaying the list of subscriptions,
 * grouped by forum category
 */
public class SubscriptionsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private final int VIEW_HEADER = 0, VIEW_ITEM = 1;

	/**
	 * Map of category -> [threads] of all our subscribed threads
	 */
	private Map<String, List<ForumThread>> threads;
	/**
	 * The list of categories we're showing
	 */
	private List<String> categories;
	/**
	 * Callbacks so we can go view the threads we're subscribed to
	 */
	private final ViewForumCallbacks viewForum;
	private final LayoutInflater inflater;

	public SubscriptionsAdapter(Context context){
		threads = new TreeMap<String, List<ForumThread>>();
		categories = new ArrayList<String>();
		inflater = LayoutInflater.from(context);
		try {
			viewForum = (ViewForumCallbacks) context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewForumCallbacks");
		}
	}

	/**
	 * Add the subscriptions forum threads and categories to be shown
	 */
	public void addSubscriptions(Subscriptions subscriptions){
		Map<String, List<ForumThread>> groups = subscriptions.groupThreadsBySection();
		threads.putAll(groups);
		//Update the category listing
		categories.clear();
		for (Map.Entry<String, List<ForumThread>> e : threads.entrySet()){
			categories.add(e.getKey());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		switch (getItemViewType(position)){
			case VIEW_HEADER:
				return getCategoryView(position, convertView, parent);
			default:
				return getThreadView(position, convertView, parent);
		}
	}

	private View getCategoryView(int position, View convertView, ViewGroup parent){
		HeaderViewHolder holder;
		if (convertView != null){
			holder = (HeaderViewHolder) convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_forum_category, parent, false);
			holder = new HeaderViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.category);
			convertView.setTag(holder);
		}
		String name = (String) getItem(position);
		holder.name.setText(name);
		return convertView;
	}

	private View getThreadView(int position, View convertView, ViewGroup parent){
		ItemViewHolder holder;
		if (convertView != null){
			holder = (ItemViewHolder) convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_forum_category, parent, false);
			holder = new ItemViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.category);
			convertView.setTag(holder);
		}
		ForumThread thread = (ForumThread) getItem(position);
		holder.name.setText(thread.getThreadTitle());
		holder.name.setTextColor(0xffff4444);
		return convertView;
	}

	@Override
	public Object getItem(int position){
		int category = 0, thread, p = position;
		while (true){
			int listLen = threads.get(categories.get(category)).size();
			//If we're in the block for the current category
			if (p - 1 - listLen < 0){
				thread = p - 1;
				break;
			}
			else {
				++category;
				p -= 1 + listLen;
			}
		}
		return p == 0 ? categories.get(category)
			: threads.get(categories.get(category)).get(thread);
	}

	@Override
	public int getViewTypeCount(){
		return 2;
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public int getCount(){
		int count = 0;
		for (Map.Entry<String, List<ForumThread>> e : threads.entrySet()){
			count += 1 + e.getValue().size();
		}
		return count;
	}

	/**
	 * Resolve the position into the proper view type, category header or thread
	 */
	@Override
	public int getItemViewType(int position){
		for (int i = 0; position > 0; ++i){
			position -= 1 + threads.get(categories.get(i)).size();
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

	/**
	 * Clear all the data being shown
	 */
	public void clear(){
		threads.clear();
		categories.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		ForumThread thread = (ForumThread) getItem(position);
		viewForum.viewThread(thread.getThreadId().intValue(), thread.getPostId().intValue());
	}

	private static class HeaderViewHolder {
		public TextView name;
	}

	private static class ItemViewHolder {
		public TextView name;
	}
}
