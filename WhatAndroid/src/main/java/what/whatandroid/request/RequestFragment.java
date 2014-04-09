package what.whatandroid.request;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import api.requests.Request;
import com.astuetz.PagerSlidingTabStrip;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;

/**
 * Fragment for showing a swipe view containing the request details and comments
 */
public class RequestFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Request> {
	private SetTitleCallback setTitle;
	private RequestPagerAdapter requestPagerAdapter;

	public static RequestFragment newInstance(int requestId){
		RequestFragment f = new RequestFragment();
		Bundle args = new Bundle();
		args.putInt(RequestActivity.REQUEST_ID, requestId);
		f.setArguments(args);
		return f;
	}

	public RequestFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallbacks");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		requestPagerAdapter = new RequestPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(requestPagerAdapter);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);
		return view;
	}

	/**
	 * Refresh the request being viewed
	 */
	public void refresh(){
		getLoaderManager().restartLoader(0, getArguments(), this);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public Loader<Request> onCreateLoader(int id, Bundle args){
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		return new RequestAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Request> loader, Request data){
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(false);
			getActivity().setProgressBarIndeterminateVisibility(false);
			if (data != null && data.getStatus()){
				setTitle.setTitle(data.getResponse().getTitle());
				requestPagerAdapter.onLoadingComplete(data);
			}
			else {
				Toast.makeText(getActivity(), "Could not load request", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Request> loader){
	}
}
