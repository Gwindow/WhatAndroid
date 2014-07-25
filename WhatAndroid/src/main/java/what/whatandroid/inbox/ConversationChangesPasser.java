package what.whatandroid.inbox;

import android.os.Bundle;

/**
 * Interface for allowing the ConversationFragment to communicate changes made
 * to the conversation back to the inbox list fragments so they can update
 * their views. Data is passed as a bundle through the InboxActivity which
 * is then queried by the inbox list fragments and consumed when the fragment
 * displaying the conversation updates its data
 */
public interface ConversationChangesPasser {
	public static final String CONVERSATION = "what.whatandroid.conversationchanges.CONVERSATION",
		STICKY = "what.whatandroid.conversationchanges.STICKY",
		UNREAD = "what.whatandroid.conversationchanges.UNREAD",
		DELETED = "what.whatandroid.conversationchanges.DELETED";

	/**
	 * Set the conversation changes bundle containing
	 * information about a conversation that was changed
	 * Changes could be any of: marked sticky/unread and/or deleted
	 *
	 * @param changes changes for the conversation, a bundle of
	 *                booleans accessed by the STICKY, UNREAD, DELETED keys
	 */
	public void setChanges(Bundle changes);

	/**
	 * Get the changes bundle containing the changed conversation information
	 */
	public Bundle getChanges();

	/**
	 * Mark the changes bundle as consumed, the inbox list fragment
	 * displaying the conversation that was changed will call this
	 * once it's updated its view to alert others that the changes
	 * have been applied
	 */
	public void consumeChanges();

	/**
	 * Check if there is any conversation changes information available
	 */
	public boolean hasChanges();
}
