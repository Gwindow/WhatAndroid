package what.whatandroid.inbox.conversation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import what.whatandroid.R;

/**
 * Dialog for managing conversation, eg making sticky,
 * marking unread and deleting
 */
public class ManageConversationDialog extends DialogFragment {
	/**
	 * Listener to receive information about changes the user
	 * is making to the conversation state
	 */
	public interface Listener {
		/**
		 * Manage the state of some conversation
		 *
		 * @param convId id of conversation to be managed
		 * @param sticky sticky state to set
		 * @param unread unread state to set
		 * @param delete true if we want to delete it
		 */
		public void manageConversation(int convId, boolean sticky, boolean unread, boolean delete);
	}

	private final static String STICKY = "what.whatandroid.conversation.STICKY",
		UNREAD = "what.whatandroid.conversation.UNREAD";

	private Listener listener;

	/**
	 * Create a new ManageConversationDialog to manage the state of the conversation
	 *
	 * @param convId id of conversation to be managed
	 * @param sticky if the conversation is currently sticky
	 * @param unread if the conversation is currently unread
	 */
	public static ManageConversationDialog newInstance(int convId, boolean sticky, boolean unread){
		ManageConversationDialog d = new ManageConversationDialog();
		Bundle args = new Bundle();
		args.putInt(ConversationFragment.CONVERSATION, convId);
		args.putBoolean(STICKY, sticky);
		args.putBoolean(UNREAD, unread);
		d.setArguments(args);
		return d;
	}

	public ManageConversationDialog(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try {
			listener = (Listener)getTargetFragment();
		}
		catch (ClassCastException e){
			throw new ClassCastException(getTargetFragment().toString() + " must implement ManageConversationDialog.Listener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
			android.R.style.Theme_Holo_Dialog_MinWidth));
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.dialog_manage_conversation, null);
		final CheckBox sticky = (CheckBox)view.findViewById(R.id.sticky);
		final CheckBox markUnread = (CheckBox)view.findViewById(R.id.mark_unread);
		final CheckBox delete = (CheckBox)view.findViewById(R.id.delete);

		Bundle args = getArguments();
		final Integer convId = args.getInt(ConversationFragment.CONVERSATION);
		sticky.setChecked(args.getBoolean(STICKY));

		builder.setView(view)
			.setTitle("Manage Conversation")
			.setPositiveButton("Select", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					listener.manageConversation(convId, sticky.isChecked(), markUnread.isChecked(), delete.isChecked());
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
		return builder.create();
	}
}
