package what.forum.thread;

import what.forum.ForumViewPager;
import what.fragments.MyFragmentPagerAdapter;
import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * 
 *
 */
public class ThreadActivity2 extends MyActivity {
	private static final String[] CONTENT = new String[] { "test", "test2" };

	private ThreadFragmentPageAdapter adapter;
	private ForumViewPager pager;
	private TitlePageIndicator indicator;

	private int threadId;
	private int threadPage;
	private int postId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		super.setContentView(R.layout.thread, false);
	}

	@Override
	public void init() {
		adapter = new ThreadFragmentPageAdapter(contentFromPageNumbers(50), getSupportFragmentManager());
		threadId = 68;
		// threadId = myBundle.getInt("id");
		/**
		 * try { page = myBundle.getInt("page"); } catch (Exception e) { page = 0; e.printStackTrace(); } try { postId =
		 * myBundle.getInt("postId"); } catch (Exception e) { postId = 0; e.printStackTrace(); }
		 */
	}

	@Override
	public void load() {
		pager = (ForumViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setPagingEnabled(false);
		indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	@Override
	public void actionbar() {
		getSupportActionBar().setTitle("one");
	}

	@Override
	public void prepare() {
		enableGestures(false);
	}

	private String[] contentFromPageNumbers(int pages) {
		String[] content = new String[pages];
		for (int i = 0; i < content.length; i++) {
			content[i] = String.valueOf(i);
		}
		return content;
	}

	private static class ThreadFragmentPageAdapter extends MyFragmentPagerAdapter {

		/**
		 * @param content
		 * @param fm
		 */
		public ThreadFragmentPageAdapter(String[] content, FragmentManager fm) {
			super(content, fm);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Fragment getItem(int position) {
			return ThreadFragment.newInstance(position);
		}

	}

}
