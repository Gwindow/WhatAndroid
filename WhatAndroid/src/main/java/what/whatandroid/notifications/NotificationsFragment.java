package what.whatandroid.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	private NotificationsPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private int pages;

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
			new ClearNotificationsTask().execute();
			return true;
		}
		return false;
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}

	private class ClearNotificationsTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params){
			return Notifications.clearNotifications();
		}

		@Override
		protected void onPreExecute(){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (isAdded()){
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (!status){
					Toast.makeText(getActivity(), "Could not clear notifications", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
