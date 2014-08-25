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
		if (topTorrents == null){
			return "loading";
		}
		//We use the captions for the titles but remove some unnecessary parts of
		//the title to make it shorter
		String title = topTorrents.getResponse().get(position).getCaption();
		if (title.startsWith("Most Active")){
			return title.substring("Most Active Torrents ".length());
		}
		return title.replace("Torrents", "");
	}

	@Override
	public int getCount(){
		if (topTorrents == null){
			return 1;
		}
		return topTorrents.getResponse().size();
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
		notifyDataSetChanged();
		for (int i = 0; i < fragments.size(); ++i){
			int pos = fragments.keyAt(i);
			fragments.get(pos).onLoadingComplete(topTorrents.getResponse().get(pos));
		}
	}
}
