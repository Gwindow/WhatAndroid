package what.whatandroid.views;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * This is identical in behavior to the FragmentStatePagerAdapter but it also provides functionality
 * for moving fragments around within the adapter so that they stay in sync with their positions in the
 * view pager after being moved.
 * <p/>
 * In order to have fragments be moved return true in hasMovedPages and return the updated positions in
 * getItemPosition. Once all the fragment positions in the array have been updated onPagesMoved will be
 * called so you can unset any status you had set for moving pages.
 */
public abstract class MovableFragmentStatePagerAdapter extends PagerAdapter {
	private static final String TAG = "MovableFragmentStatePagerAdapter";

	private final FragmentManager fragmentManager;
	private FragmentTransaction currentTransaction = null;

	private ArrayList<Fragment.SavedState> savedStates = new ArrayList<Fragment.SavedState>();
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private Fragment currentPrimaryItem = null;

	public MovableFragmentStatePagerAdapter(FragmentManager fm){
		fragmentManager = fm;
	}

	/**
	 * Called to check if any of the fragments have changed their positions in the adapter
	 *
	 * @return true if some fragments have changed their position
	 */
	public abstract boolean hasMovedPages();

	/**
	 * Called after fragments have been moved to their proper positions in the parent adapter
	 * You should unset any flags being used to indicate whether or not there are moved pages
	 * in this function
	 */
	public abstract void onPagesMoved();

	/**
	 * This function will return the fragment to be displayed at some position in the
	 * pager adapter
	 *
	 * @param position position the fragment is being instantiated at
	 * @return the fragment to show at this position
	 */
	public abstract Fragment getItem(int position);

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		//If we've got the item already then just return it
		if (fragments.size() > position){
			Fragment f = fragments.get(position);
			if (f != null){
				return f;
			}
		}

		if (currentTransaction == null){
			currentTransaction = fragmentManager.beginTransaction();
		}

		Fragment f = getItem(position);
		if (savedStates.size() > position){
			Fragment.SavedState state = savedStates.get(position);
			if (state != null){
				f.setInitialSavedState(state);
			}
		}
		while (fragments.size() <= position){
			fragments.add(null);
		}
		f.setMenuVisibility(false);
		f.setUserVisibleHint(false);
		fragments.set(position, f);
		currentTransaction.add(container.getId(), f);
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		Fragment f = (Fragment)object;
		if (currentTransaction == null){
			currentTransaction = fragmentManager.beginTransaction();
		}
		while (savedStates.size() <= position){
			savedStates.add(null);
		}
		savedStates.set(position, fragmentManager.saveFragmentInstanceState(f));
		fragments.set(position, null);
		currentTransaction.remove(f);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object){
		Fragment f = (Fragment)object;
		if (f != currentPrimaryItem){
			if (currentPrimaryItem != null){
				currentPrimaryItem.setMenuVisibility(false);
				currentPrimaryItem.setUserVisibleHint(false);
			}
			if (f != null){
				f.setMenuVisibility(true);
				f.setUserVisibleHint(true);
			}
			currentPrimaryItem = f;
		}
	}

	@Override
	public void startUpdate(ViewGroup container){
		//If some pages have moved in the adapter update our fragment array to match the new state
		if (hasMovedPages()){
			for (int i = 0; i < fragments.size(); ++i){
				if (fragments.get(i) != null){
					int pos = getItemPosition(fragments.get(i));
					//Anything greater than unchanged is a valid position to move the fragment to
					if (pos > POSITION_UNCHANGED){
						while (fragments.size() <= pos){
							fragments.add(null);
						}
						//If we're moving to the same place we are now there's nothing to do
						if (fragments.get(i) != fragments.get(pos)){
							fragments.set(pos, fragments.get(i));
							fragments.set(i, null);
						}
					}
				}
			}
			onPagesMoved();
		}
	}

	@Override
	public void finishUpdate(ViewGroup container){
		if (currentTransaction != null){
			currentTransaction.commitAllowingStateLoss();
			currentTransaction = null;
			fragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object){
		return ((Fragment)object).getView() == view;
	}

	@Override
	public Parcelable saveState(){
		Bundle state = null;
		if (savedStates.size() > 0){
			state = new Bundle();
			Fragment.SavedState[] fss = new Fragment.SavedState[savedStates.size()];
			savedStates.toArray(fss);
			state.putParcelableArray("states", fss);
		}
		for (int i = 0; i < fragments.size(); i++){
			Fragment f = fragments.get(i);
			if (f != null){
				if (state == null){
					state = new Bundle();
				}
				String key = "f" + i;
				fragmentManager.putFragment(state, key, f);
			}
		}
		return state;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader){
		if (state != null){
			Bundle bundle = (Bundle)state;
			bundle.setClassLoader(loader);
			Parcelable[] states = bundle.getParcelableArray("states");
			savedStates.clear();
			fragments.clear();
			if (states != null){
				for (Parcelable s : states){
					savedStates.add((Fragment.SavedState)s);
				}
			}
			Iterable<String> keys = bundle.keySet();
			for (String key : keys){
				if (key.startsWith("f")){
					int index = Integer.parseInt(key.substring(1));
					Fragment f = fragmentManager.getFragment(bundle, key);
					if (f != null){
						while (fragments.size() <= index){
							fragments.add(null);
						}
						f.setMenuVisibility(false);
						fragments.set(index, f);
					}
					else {
						Log.w(TAG, "Bad fragment at key " + key);
					}
				}
			}
		}
	}
}
