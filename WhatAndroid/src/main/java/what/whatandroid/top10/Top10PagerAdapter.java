package what.whatandroid.top10;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import api.top.TopTorrents;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Displays a swipe-able view of the various top torrent category listings
 */
public class Top10PagerAdapter extends FragmentStatePagerAdapter implements LoadingListener<TopTorrents> {
	/**
	 * The top torrents being shown
	 */
	private TopTorrents topTorrents;

	/**
	 * The various top 10 list fragments showing the items
	 * for each category
	 */
	private SparseArray<Top10ListFragment> fragments;

	/**
	 * The various top 10 categories being shown.
	 * TODO: Make these categories depend on the loaded data? It's a bit more
	 * of a hassle to do but will be more robust in case categories change down the line
	 */
	private String[] titles = {"Day", "Week", "All Time",
		"Most Snatched", "Most Data Transferred", "Best Seeded"};

	public Top10PagerAdapter(FragmentManager fm){
		super(fm);
		fragments = new SparseArray<Top10ListFragment>();
	}

	@Override
	public Fragment getItem(int position){
		return new Top10ListFragment();
	}

	@Override
	public CharSequence getPageTitle(int position){
		return titles[position];
	}

	@Override
	public int getCount(){
		return titles.length;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		Top10ListFragment f = (Top10ListFragment)super.instantiateItem(container, position);
		if (topTorrents != null){
			f.onLoadingComplete(topTorrents.getResponse().get(position));
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
	public void onLoadingComplete(TopTorrents data){
		topTorrents = data;
		for (int i = 0; i < fragments.size(); ++i){
			int pos = fragments.keyAt(i);
			fragments.get(pos).onLoadingComplete(topTorrents.getResponse().get(pos));
		}
	}
}
