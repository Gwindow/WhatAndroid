package what.whatandroid;

import android.support.v4.app.Fragment;

/**
 * Interface implemented by fragment containers to allow
 * the contained fragments to request that the host transitions
 * to a new fragment or adds a new fragment to the container
 */
public interface FragmentHost {
	/**
	 * The fragment host will replace the current fragment with the passed in one
	 * along with changing the title to the desired title and if removeNav
	 * is true will remove the old fragment from the navbar
	 * @param fragment fragment to transition to
	 * @param title new title to display
	 * @param removeNav true if the old fragment should be removed from the nav bar
	 */
	public void replaceFragment(Fragment fragment, String title, Boolean removeNav);
	//public void addFragment();
}
