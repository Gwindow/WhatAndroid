package what.whatandroid.forums.thread;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import api.forum.thread.ForumThread;
import api.soup.MySoup;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.views.MovableFragmentStatePagerAdapter;

/**
 * Adapter for swiping through the pages of a thread
 */
public class ThreadPagerAdapter extends MovableFragmentStatePagerAdapter implements OnLoggedInCallback, LoadingListener<ForumThread> {
	private LoadingListener<ForumThread> listener;
	private SparseArray<ThreadListFragment> fragments = new SparseArray<ThreadListFragment>();
	/**
	 * The number of pages in the thread, the thread being viewed,
	 * the post id to jump to (if any) and the page that post is on
	 */
	private int pages, thread, postId, postPage;
	/**
	 * View pager we're given when trying to refresh posts and jump to the last one
	 * also used to track if we're refreshing posts to show our new reply as it
	 * will only be non-null if we're doing that
	 */
	private ViewPager viewPager;

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
		this.pages = pages;
		this.thread = thread;
		this.postId = postId;
		this.postPage = -1;
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
		return pages == 0 ? "Loading" : "page " + (position + 1) + " of " + pages;
	}

	@Override
	public int getCount(){
		return pages == 0 ? 1 : pages;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		ThreadListFragment f = (ThreadListFragment)super.instantiateItem(container, position);
		if (MySoup.isLoggedIn()){
			f.onLoggedIn();
		}
		f.setListener(this);
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

	@Override
	public int getItemPosition(Object object){
		if (hasMovedPages() && object == fragments.get(0)){
			fragments.put(postPage, fragments.get(0));
			return postPage;
		}
		return POSITION_UNCHANGED;
	}

	@Override
	public boolean hasMovedPages(){
		return postPage != -1;
	}

	@Override
	public void onPagesMoved(){
		postPage = -1;
	}

	/**
	 * Update the view pager to show all pages in the thread, now that we know how many
	 * there are
	 *
	 * @param data the loaded data
	 */
	@Override
	public void onLoadingComplete(ForumThread data){
		//If this is the first page we've loaded update the listener and jump to a post if desired
		if (pages == 0){
			pages = data.getPages();
			//If we're jumping to a post get the page that it's on so we can jump to it
			if (postId != -1){
				postPage = data.getPage() - 1;
				postId = -1;
			}
			notifyDataSetChanged();
			listener.onLoadingComplete(data);
		}
		//If we're jumping to the last post after making one and our new post has
		//added a new page on the thread jump to this new page
		else if (viewPager != null && viewPager.getCurrentItem() == data.getPage() - 1 && data.hasNextPage()){
			pages = data.getPages();
			notifyDataSetChanged();
			viewPager.setCurrentItem(pages - 1);
			fragments.get(viewPager.getCurrentItem()).reloadPosts();
			viewPager = null;
		}
	}

	/**
	 * Jump the adapter to the last post in the thread and refresh the last page
	 * This should be used when adding a new post to the thread and we want
	 * to update the last page to see any new posts (including our own)
	 *
	 * @param pager The viewpager showing the data from the adapter, we need
	 *              this to be able to update the selected page appropriately
	 */
	public void getNewPosts(ViewPager pager){
		viewPager = pager;
		//Jump to the last page we know of, note that we may need to go one more page
		//if the new post created a new page. This is handled in our loading listener
		if (pages > 0 && viewPager.getCurrentItem() != pages - 1){
			viewPager.setCurrentItem(pages - 1);
		}
		fragments.get(viewPager.getCurrentItem()).reloadPosts();
	}
}
