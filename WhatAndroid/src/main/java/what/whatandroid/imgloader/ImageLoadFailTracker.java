package what.whatandroid.imgloader;

import java.util.HashSet;

/**
 * Tracks art that has failed loading so that we can skip trying to reload it
 * when viewing lists of albums or similar
 */
public class ImageLoadFailTracker {
	private HashSet<String> failedImages;

	public ImageLoadFailTracker(){
		failedImages = new HashSet<String>();
	}

	/**
	 * Add an image url that failed loading to be tracked
	 */
	public void addFailed(String img){
		failedImages.add(img);
	}

	/**
	 * Check if some img url was previously loaded but failed
	 */
	public boolean failed(String img){
		return failedImages.contains(img);
	}
}
