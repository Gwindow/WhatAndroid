package what.whatandroid.forums.forum;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import api.forum.forum.Forum;
import api.soup.MySoup;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Adapter for swiping through the pages of the forum
 */
public class ForumPagerAdapter extends FragmentStatePagerAdapter implements OnLoggedInCallback, LoadingListener<Forum> {
	private LoadingListener<Forum> listener;
	private SparseArray<ForumListFragment> fragments;
	private int pages, forum;

	/**
	 * Create a fragment pager view displaying the paged lists of posts in the forum
	 *
	 * @param pages number of pages to display initially. Will be updated to the total
	 *              amount after loading the first page
	 * @param forum forum id to display
	 */
	public ForumPagerAdapter(FragmentManager fm, int pages, int forum){
		super(fm);
		fragments = new SparseArray<ForumListFragment>();
		this.pages = pages;
		this.forum = forum;
	}

	@Override
	public Fragment getItem(int position){
		//Page numbers start at 1 but positions are 0-indexed
		return ForumListFragment.newInstance(forum, position + 1);
	}

	@Override
	public CharSequence getPageTitle(int position){
		return "page " + (position + 1) + " of " + pages;
	}

	@Override
	public int getCount(){
		return pages;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		ForumListFragment f = (ForumListFragment)super.instantiateItem(container, position);
		if (MySoup.isLoggedIn()){
			f.onLoggedIn();
		}
		//We need to load a page to figure out how many pages there are in total, so listen to the first one
		if (position == 0){
			f.setListener(this);
		}
		fragments.put(position, f);
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		fragments.remove(position);
	}

	@Override
	public void onLoggedIn(){
		for (int i = 0; i < fragments.size(); ++i){
			fragments.valueAt(i).onLoggedIn();
		}
	}

	public void refresh() {
		for (int i = 0; i < fragments.size(); ++i) {
			fragments.valueAt(i).refresh();
		}
	}

	public void setLoadingListener(LoadingListener<Forum> listener){
		this.listener = listener;
	}

	/**
	 * Update the view pager to show all pages in the forum, now that we know how many
	 * there are
	 *
	 * @param data the loaded data
	 */
	@Override
	public void onLoadingComplete(Forum data){
		pages = data.getPages();
		notifyDataSetChanged();
		listener.onLoadingComplete(data);
	}

    /**
     * Set the forum layout to light/default layout for all used fragments.
     * @param set True if set light version.
     */
    public void setUseLightLayout(boolean set){
        for (int i = 0; i < fragments.size(); ++i){
            fragments.valueAt(i).setUseLightLayout(set);
        }
    }
}
