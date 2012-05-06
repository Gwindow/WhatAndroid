package what.forum.section;

import what.forum.ForumViewPager;
import what.fragments.MyFragmentPagerAdapter;
import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class SectionActivity2 extends MyActivity {
	private static final String[] CONTENT = new String[] { "test1", "hihihi", "A", "Test", };

	private SectionFragmentPageAdapter adapter;
	private ForumViewPager pager;
	private TitlePageIndicator indicator;

	private int threadId;
	private int threadPage;
	private int postId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		// super.setContentView(R.layout.section, false);
	}

	@Override
	public void init() {
		adapter = new SectionFragmentPageAdapter(CONTENT, getSupportFragmentManager());
		// adapter.threadId = 68;
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

	private class SectionFragmentPageAdapter extends MyFragmentPagerAdapter {

		/**
		 * @param content
		 * @param fm
		 */
		public SectionFragmentPageAdapter(String[] content, FragmentManager fm) {
			super(content, fm);
		}

		@Override
		public Fragment getItem(int position) {
			return null;
			// return SectionFragment.newInstance(content[position % content.length]);
		}
	}

}
