package what.fragments;

import api.util.Tuple;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:55:05 PM
 */
public class CoverArtFragment extends SherlockFragment {
	private Tuple<String, Integer>[] images;

	public CoverArtFragment(Tuple<String, Integer>[] images) {
		this.images = images;
	}
}
