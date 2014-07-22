package what.whatandroid.inbox;

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
 * The inbox fragment displays the paged list of conversations in
 * the user's inbox so they can pick one to view or manage them
 */
public class InboxFragment extends Fragment implements OnLoggedInCallback {
	private static String PAGES = "what.whatandroid.inboxfragment.PAGES";

	/**
	 * The adapter to alert when we've logged in to the site
	 */
	private OnLoggedInCallback loginListener;

	/**
	 * Create a fragment displaying the user's inbox
	 */
	public InboxFragment(){
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		InboxPagerAdapter pagerAdapter = new InboxPagerAdapter(getChildFragmentManager(), 1);
		viewPager.setAdapter(pagerAdapter);
		loginListener = pagerAdapter;
		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onLoggedIn(){
		loginListener.onLoggedIn();
	}
}
