package what.forum.section;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import what.forum.ForumViewPager;
import what.fragments.MyFragmentPagerAdapter;
import what.gui.ErrorToast;
import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import api.forum.section.Section;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * @author Gwindow
 * @since May 5, 2012 5:55:53 PM
 */
public class SectionActivity2 extends MyActivity {
	private SectionFragmentPageAdapter adapter;
	private ForumViewPager pager;
	private TitlePageIndicator indicator;
	public HashMap<Integer, SectionFragment> fragmentMap;
	private Section section;
	private int sectionId;
	private int sectionPage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow);
		super.setContentView(R.layout.section, false);
	}

	@Override
	public void init() {
		sectionId = myBundle.getInt("sectionId");
		sectionPage = 1;

		try {
			section = new SectionLoader().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		if (section.getStatus()) {
			adapter =
					new SectionFragmentPageAdapter(SectionFragmentPageAdapter.contentFromPageNumbers(section.getLastPage()),
							getSupportFragmentManager());
		} else {
			ErrorToast.show(this, getClass());
			finish();
		}
	}

	@Override
	public void load() {
		pager = (ForumViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setPagingEnabled(false);
		indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setCurrentItem(sectionPage - 1);
	}

	@Override
	public void actionbar() {
		getSupportActionBar().setTitle(section.getResponse().getForumName());
	}

	@Override
	public void prepare() {
		enableGestures(false);

		fragmentMap.get(sectionPage).populate(section.getResponse().getThreads());

		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				sectionPage = position + 1;
				try {
					section = new SectionLoader().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				fragmentMap.get(position + 1).populate(section.getResponse().getThreads());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private class SectionFragmentPageAdapter extends MyFragmentPagerAdapter {
		/**
		 * @param content
		 * @param fm
		 */
		public SectionFragmentPageAdapter(String[] content, FragmentManager fm) {
			super(content, fm);
			fragmentMap = new HashMap<Integer, SectionFragment>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = SectionFragment.newInstance(position + 1, sectionId);
			fragmentMap.put(position + 1, (SectionFragment) fragment);
			return fragment;
		}
	}

	private class SectionLoader extends AsyncTask<Void, Void, Section> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(SectionActivity2.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Section doInBackground(Void... params) {
			return Section.sectionFromIdAndPage(sectionId, sectionPage);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Section result) {
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

}
