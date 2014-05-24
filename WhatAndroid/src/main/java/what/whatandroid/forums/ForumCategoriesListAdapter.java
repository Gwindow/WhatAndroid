package what.whatandroid.forums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import api.forum.categories.Category;
import api.util.Tuple;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Displays a list of the forum categories and the most recently replied to
 * post in each forum
 */
public class ForumCategoriesListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private final int VIEW_HEADER = 0, VIEW_ITEM = 1;
	/**
	 * List of categories and forums being shown
	 */
	List<Category> categories;

	/**
	 * Callbacks to open a specific forum to view
	 */
	private ViewForumCallbacks viewForum;
	private final LayoutInflater inflater;

	public ForumCategoriesListAdapter(Context context){
		categories = new ArrayList<Category>();
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
			holder.size = (TextView)convertView.findViewById(R.id.size);
			convertView.setTag(holder);
		}
		Tuple<Integer, Integer> indices = getIndices(position);

		Category category = getItem(indices.getA());
		if (getItemViewType(position) == VIEW_HEADER){
			holder.name.setText(category.getCategoryName());
			holder.size.setText(indices.getA() + ", " + VIEW_HEADER);
		}
		else {
			holder.name.setText(category.getCategoryName() + " - "
				+ category.getForums().get(indices.getB()).getForumName());
			holder.size.setText(indices.getA() + ", " + indices.getB() + " - " + VIEW_ITEM);
		}
		return convertView;
	}

	@Override
	public Category getItem(int position){
		return categories.get(position);
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
			Category c = getItem(i);
			position -= 1 + c.getForums().size();
		}
		return position == 0 ? VIEW_HEADER : VIEW_ITEM;
	}

	/**
	 * Get the category and forum indices for some forum entry in the list
	 * Note: if the type at position is VIEW_HEADER the forum index is likely invalid
	 *
	 * @return Tuple{category index, forum index}
	 */
	private Tuple<Integer, Integer> getIndices(int position){
		int i = 0;
		while (true){
			Category c = getItem(i);
			position -= 1 + c.getForums().size();
			if (position > -1){
				++i;
			}
			if (position <= 0){
				break;
			}
		}
		int forumPos = position + categories.get(i).getForums().size();
		return new Tuple<Integer, Integer>(i, forumPos);
	}

	public void addAll(Collection<Category> collection){
		categories.addAll(collection);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		System.out.println("Position " + position + " clicked");
	}

	private static class ViewHolder {
		public TextView name, size;
	}
}
