package what.whatandroid.forums.thread;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import api.forum.thread.ForumThread;
import api.soup.MySoup;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Adapter for swiping through the pages of a thread
 */
public class ThreadPagerAdapter extends FragmentStatePagerAdapter implements OnLoggedInCallback, LoadingListener<ForumThread> {
	private LoadingListener<ForumThread> listener;
	private SparseArray<ThreadListFragment> fragments;
	private int pages, thread, postId;

	/**
	 * Create a fragment pager view displaying the paged lists of posts in the thread
	 *
	 * @param pages  number of pages to display initially. Will be updated to the total
	 *               amount after loading the first page
	 * @param thread thread id to display
	 * @param postId post id to jump to, or -1 to ignore
	 */
	public ThreadPagerAdapter(FragmentManager fm, int pages, int thread, int postId){
		super(fm);
		fragments = new SparseArray<ThreadListFragment>();
		this.pages = pages;
		this.thread = thread;
		this.postId = postId;
	}

	@Override
	public Fragment getItem(int position){
		//Page numbers start at 1 but positions are 0-indexed
		if (position == 0 && postId != -1){
			return ThreadListFragment.newInstancePost(thread, postId);
		}
		return ThreadListFragment.newInstance(thread, position + 1);
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
		ThreadListFragment f = (ThreadListFragment)super.instantiateItem(container, position);
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

	public void setLoadingListener(LoadingListener<ForumThread> listener){
		this.listener = listener;
	}

	/**
	 * Update the view pager to show all pages in the thread, now that we know how many
	 * there are
	 *
	 * @param data the loaded data
	 */
	@Override
	public void onLoadingComplete(ForumThread data){
		pages = data.getPages();
		notifyDataSetChanged();
		listener.onLoadingComplete(data);
	}
}
