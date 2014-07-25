package what.whatandroid.inbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import api.inbox.inbox.Inbox;
import api.soup.MySoup;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Adapter to display the paged lists of conversations in the inbox
 */
public class InboxPagerAdapter extends FragmentStatePagerAdapter implements OnLoggedInCallback, LoadingListener<Inbox> {
	/**
	 * Loading listener to alert when we've loaded the inbox
	 * The InboxFragment uses this to find and save the total number of pages
	 * Could I instead use save/restoreState?
	 */
	private SparseArray<InboxListFragment> fragments;
	private int pages;

	public InboxPagerAdapter(FragmentManager fm, int pages){
		super(fm);
		fragments = new SparseArray<InboxListFragment>();
		this.pages = pages;
	}

	@Override
	public Fragment getItem(int position){
		//Page numbers start at 1 but positions are 0-indexed
		return InboxListFragment.newInstance(position + 1);
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
		InboxListFragment f = (InboxListFragment)super.instantiateItem(container, position);
		if (MySoup.isLoggedIn()){
			f.onLoggedIn();
		}
		//We need to load the first page to figure out how many pages there are in total
		//so listen to the first one
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
	public void onLoadingComplete(Inbox data){
		pages = data.getResponse().getPages().intValue();
		notifyDataSetChanged();
	}

	@Override
	public void onLoggedIn(){
		for (int i = 0; i < fragments.size(); ++i){
			fragments.valueAt(i).onLoggedIn();
		}
	}
}
