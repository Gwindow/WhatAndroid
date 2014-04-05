package what.whatandroid.callbacks;

/**
 * Interface that fragments displaying loaded data can implement to be
 * notified when loading is finished and receive the data they should show
 */
public interface LoadingListener<T> {
	/**
	 * Get notified that the group has finished loading and receive the loaded group
	 *
	 * @param data the loaded data
	 */
	public void onLoadingComplete(T data);
}
