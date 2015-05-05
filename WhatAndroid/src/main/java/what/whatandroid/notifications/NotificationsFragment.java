package what.whatandroid.notifications;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import api.notifications.Notifications;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment containing the swipe view of notification pages
 */
public class NotificationsFragment extends Fragment implements OnLoggedInCallback {
	public static final String CLEAR_NOTIFICATION_FILTER = "NotificationsFragment_recevier";
	private NotificationsPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private int pages;
	private ClearNotificationsReceiver receiver;

	public NotificationsFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		pages = 1;
		if (savedInstanceState != null){
			pages = savedInstanceState.getInt("PAGES");
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		receiver = new ClearNotificationsReceiver();
		LocalBroadcastManager.getInstance(activity)
			.registerReceiver(receiver, new IntentFilter(CLEAR_NOTIFICATION_FILTER));
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (receiver != null) {
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.pager);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.notifications, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.clear){
			viewPager.setCurrentItem(0);
			pagerAdapter.clearNotifications();
			Intent clearNotifications = new Intent(getActivity(), ClearNotificationsService.class);
			receiver.serProgressBar();
			getActivity().startService(clearNotifications);
			return true;
		}
		return false;
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}

	private class ClearNotificationsReceiver extends BroadcastReceiver {
		public void serProgressBar(){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onReceive(Context receiverContext, Intent receiverIntent){
			boolean status = receiverIntent.getBooleanExtra("status", false);
			if (isAdded()){
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (!status){
					Toast.makeText(getActivity(), "Could not clear notifications", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public static class ClearNotificationsService extends IntentService {
		public ClearNotificationsService() {
			super("ClearNotificationsService");
		}

		public void onHandleIntent(Intent intent) {
			Intent resultIntent = new Intent(CLEAR_NOTIFICATION_FILTER);
			resultIntent.putExtra("status", Notifications.clearNotifications());
			LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
			return;
		}
	}
}
