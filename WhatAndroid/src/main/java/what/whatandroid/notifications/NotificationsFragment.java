package what.whatandroid.notifications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment containing the swipe view of notification pages
 */
public class NotificationsFragment extends Fragment implements OnLoggedInCallback {
	private NotificationsPagerAdapter pagerAdapter;
	private int pages;

	public NotificationsFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		pages = 1;
		if (savedInstanceState != null){
			pages = savedInstanceState.getInt("PAGES");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		pagerAdapter = new NotificationsPagerAdapter(getChildFragmentManager(), pages);
		viewPager.setAdapter(pagerAdapter);
		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("PAGES", pagerAdapter.getCount());
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}
}
