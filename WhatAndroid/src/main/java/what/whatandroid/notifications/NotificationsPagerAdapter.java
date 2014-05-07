package what.whatandroid.notifications;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import api.notifications.Notifications;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Adapter for swiping through the pages of notifications
 */
public class NotificationsPagerAdapter extends FragmentStatePagerAdapter implements OnLoggedInCallback, LoadingListener<Notifications> {
	private SparseArray<NotificationsListFragment> fragments;
	private int pages;
	private boolean loggedIn;

	public NotificationsPagerAdapter(FragmentManager fm, int pages){
		super(fm);
		fragments = new SparseArray<NotificationsListFragment>();
		this.pages = pages;
		loggedIn = false;
	}

	@Override
	public Fragment getItem(int position){
		return NotificationsListFragment.newInstance(position + 1);
	}

	@Override
	public int getCount(){
		return pages;
	}

	@Override
	public CharSequence getPageTitle(int position){
		return "page " + (position + 1) + " of " + pages;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		NotificationsListFragment f = (NotificationsListFragment)super.instantiateItem(container, position);
		if (loggedIn){
			f.onLoggedIn();
		}
		//We need to load a page to figure out how many pages there are in total, so we listen to the first one
		if (position == 0){
			f.setLoadingListener(this);
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
	public void onLoadingComplete(Notifications data){
		pages = data.getResponse().getPages().intValue();
		if (pages == 0){
			pages++;
		}
		notifyDataSetChanged();
	}

	public void clearNotifications(){
		pages = 1;
		notifyDataSetChanged();
		for (int i = 0; i < fragments.size(); ++i){
			fragments.valueAt(i).clearNotifications();
		}
	}

	@Override
	public void onLoggedIn(){
		loggedIn = true;
		for (int i = 0; i < fragments.size(); ++i){
			fragments.valueAt(i).onLoggedIn();
		}
	}
}
