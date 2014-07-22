package what.whatandroid.inbox.conversation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import api.inbox.conversation.Conversation;

/**
 * Async loader to load a conversation
 */
public class ConversationAsyncLoader extends AsyncTaskLoader<Conversation> {
	private Conversation conversation;
	private int id;

	public ConversationAsyncLoader(Context context, Bundle args){
		super(context);
		id = args.getInt(ConversationFragment.CONVERSATION);
	}

	@Override
	public Conversation loadInBackground(){
		if (conversation == null){
			while (true){
				conversation = Conversation.conversation(id);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (conversation != null && !conversation.getStatus() && conversation.getError() != null
					&& conversation.getError().equalsIgnoreCase("rate limit exceeded")){
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException e){
						Thread.currentThread().interrupt();
					}
				}
				else {
					break;
				}
			}
		}
		return conversation;
	}

	@Override
	protected void onStartLoading(){
		if (conversation != null){
			deliverResult(conversation);
		}
		else {
			forceLoad();
		}
	}
}
