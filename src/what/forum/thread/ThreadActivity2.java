package what.forum.thread;

import java.util.HashMap;
import java.util.Map;

import what.forum.ForumViewPager;
import what.fragments.MyFragmentPagerAdapter;
import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * 
 *
 */
public class ThreadActivity2 extends MyActivity {
	private ThreadFragmentPageAdapter adapter;
	private ForumViewPager pager;
	private TitlePageIndicator indicator;
	private Map<Integer, ThreadFragment> fragmentMap;

	private int threadId;
	private int threadPage;
	private int postId;

	private String threadTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		super.setContentView(R.layout.thread, false);
	}

	@Override
	public void init() {
		threadId = myBundle.getInt("threadId");
		threadTitle = myBundle.getString("threadTitle");

		try {
			threadPage = myBundle.getInt("threadPage");
		} catch (Exception e) {
			threadPage = 0;
		}
		try {
			postId = myBundle.getInt("postId");
		} catch (Exception e) {
			postId = 0;
		}

		adapter = new ThreadFragmentPageAdapter(ThreadFragmentPageAdapter.contentFromPageNumbers(5), getSupportFragmentManager());

	}

	@Override
	public void load() {
		pager = (ForumViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setPagingEnabled(false);
		indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				fragmentMap.get(position).test();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	@Override
	public void actionbar() {
		getSupportActionBar().setTitle("ThreadTitle");
	}

	@Override
	public void prepare() {
		enableGestures(false);
	}

	private class ThreadFragmentPageAdapter extends MyFragmentPagerAdapter {
		/**
		 * @param content
		 * @param fm
		 */
		public ThreadFragmentPageAdapter(String[] content, FragmentManager fm) {
			super(content, fm);
			fragmentMap = new HashMap<Integer, ThreadFragment>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = ThreadFragment.newInstance(position + 1);
			fragmentMap.put(position + 1, (ThreadFragment) fragment);
			return fragment;
		}
	}

}
