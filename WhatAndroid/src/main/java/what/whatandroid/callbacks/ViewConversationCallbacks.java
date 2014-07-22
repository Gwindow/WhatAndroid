package what.whatandroid.callbacks;

/**
 * Callbacks for requesting that a conversation be viewed
 */
public interface ViewConversationCallbacks {
	/**
	 * Request the implementer to displaying the inbox conversation with some id
	 */
	public void viewConversation(int id);
}
