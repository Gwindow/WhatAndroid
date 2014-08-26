package what.whatandroid.top10;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import api.soup.MySoup;
import api.top.TopTorrents;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment responsible for loading the top 10 torrent data
 * and displaying a view pager containing fragments displaying
 * lists of the torrents for each category
 */
public class Top10Fragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<TopTorrents> {
	private static final String VIEW_PAGER_STATE = "what.whatandroid.top10.top10fragment.VIEW_PAGER_STATE";
	/**
	 * Adapter displaying the categories of the top 10 torrent lists
	 */
	private Top10PagerAdapter pagerAdapter;
	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	/**
	 * Since we're setting the number of items in the view pager
	 * based on the data loaded we need to restore the selected
	 * page after setting the data up when coming back from an
	 * orientation change
	 */
	private Parcelable viewPagerState;

	public Top10Fragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null){
			viewPagerState = savedInstanceState.getParcelable(VIEW_PAGER_STATE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_tabs, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
		pagerAdapter = new Top10PagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		tabs.setViewPager(viewPager);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, null, this);
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelable(VIEW_PAGER_STATE, viewPager.onSaveInstanceState());
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, null, this);
		}
	}

	@Override
	public Loader<TopTorrents> onCreateLoader(int id, Bundle args){
		return new Top10AsyncLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<TopTorrents> loader, TopTorrents data){
		if (data == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load top torrents", Toast.LENGTH_LONG).show();
		}
		else {
			pagerAdapter.onLoadingComplete(data);
			tabs.notifyDataSetChanged();
			if (viewPagerState != null){
				viewPager.onRestoreInstanceState(viewPagerState);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<TopTorrents> loader){
	}
}
