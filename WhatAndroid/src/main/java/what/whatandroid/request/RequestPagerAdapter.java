package what.whatandroid.request;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import api.requests.Request;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Fragment pager adapter to show the request details and comments
 */
public class RequestPagerAdapter extends FragmentPagerAdapter implements LoadingListener<Request> {
	/**
	 * The request details and comments fragments being shown
	 */
	private RequestDetailFragment detail;
	private RequestCommentsFragment comments;
	private Request request;

	public RequestPagerAdapter(FragmentManager fm){
		super(fm);
	}

	@Override
	public Fragment getItem(int position){
		switch (position){
			case 0:
				return new RequestDetailFragment();
			default:
				return new RequestCommentsFragment();
		}
	}

	@Override
	public int getCount(){
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position){
		switch (position){
			case 0:
				return "Request";
			default:
				return "Comments";
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		Fragment f = (Fragment)super.instantiateItem(container, position);
		if (position == 0){
			detail = (RequestDetailFragment)f;
			if (request != null){
				detail.onLoadingComplete(request);
			}
		}
		else {
			comments = (RequestCommentsFragment)f;
			if (request != null){
				comments.onLoadingComplete(request);
			}
		}
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		switch (position){
			case 0:
				detail = null;
			default:
				comments = null;
		}
	}

	@Override
	public void onLoadingComplete(Request data){
		request = data;
		if (detail != null){
			detail.onLoadingComplete(data);
		}
		if (comments != null){
			comments.onLoadingComplete(data);
		}
	}
}
