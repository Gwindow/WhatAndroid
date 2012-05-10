package what.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.TitleProvider;

/**
 * The Class MyFragmentPagerAdapter.
 * 
 * @author Gwindow
 * @since May 6, 2012 1:40:19 PM
 */
public abstract class MyFragmentPagerAdapter extends FragmentPagerAdapter implements TitleProvider {

	/** The content. */
	protected String[] content;

	/** The count. */
	private int mCount;

	protected FragmentManager fragmentManager;

	/**
	 * Instantiates a new my fragment pager adapter.
	 * 
	 * @param content
	 *            the content
	 * @param fm
	 *            the fm
	 */
	public MyFragmentPagerAdapter(String[] content, FragmentManager fm) {
		super(fm);
		this.content = content;
		this.fragmentManager = fm;
		this.mCount = content.length;
	}

	public static String[] contentFromPageNumbers(int pages) {
		String[] content = new String[pages];
		for (int i = 0; i < content.length; i++) {
			content[i] = String.valueOf(i + 1);
		}
		return content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Fragment getItem(int position);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		return mCount;
	}

	/**
	 * Sets the count.
	 * 
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		mCount = count;
		notifyDataSetChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(int position) {
		return content[position % content.length];
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public String[] getContent() {
		return content;
	}
}
