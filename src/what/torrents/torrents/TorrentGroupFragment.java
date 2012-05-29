package what.torrents.torrents;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since May 28, 2012 6:19:18 PM
 */
public class TorrentGroupFragment extends SherlockFragment {
	private String title;

	/**
	 * @param string
	 * @return
	 */
	public static Fragment newInstance(String title) {
		return new TorrentGroupFragment(title);
	}

	public TorrentGroupFragment(String title) {
		this.title = title;
	}

}
