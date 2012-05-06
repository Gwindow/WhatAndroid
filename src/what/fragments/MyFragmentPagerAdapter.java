package what.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.viewpagerindicator.TitleProvider;

/**
 * 
 *
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements TitleProvider {
	protected String[] content;
	private int mCount;

	public MyFragmentPagerAdapter(String[] content, FragmentManager fm) {
		super(fm);
		this.content = content;
		this.mCount = content.length;
	}

	@Override
	public Fragment getItem(int position) {
		return MyFragment.newInstance(content[position % content.length]);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

	@Override
	public String getTitle(int position) {
		return content[position % content.length];
	}

	/**
	 * @return the content
	 */
	public String[] getContent() {
		return content;
	}
}
