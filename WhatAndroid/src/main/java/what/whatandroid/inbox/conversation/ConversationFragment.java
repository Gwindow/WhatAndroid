package what.whatandroid.inbox.conversation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import api.inbox.conversation.Conversation;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.AddQuoteCallback;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.comments.CommentsAdapter;
import what.whatandroid.forums.thread.ReplyDialogFragment;
import what.whatandroid.inbox.ConversationChangesPasser;

/**
 * Fragment for displaying the list of messages in a conversation
 */
public class ConversationFragment extends Fragment implements OnLoggedInCallback,
	AddQuoteCallback, LoaderManager.LoaderCallbacks<Conversation>, ReplyDialogFragment.ReplyDialogListener,
	ManageConversationDialog.Listener {

	public static final String CONVERSATION = "what.whatandroid.conversationfragment.CONVERSATION";
	private static final String SCROLL_STATE = "what.whatandroid.conversationfragment.SCROLL_STATE";

	private ProgressBar loadingIndicator;
	/**
	 * The conversation being viewed
	 */
	private Conversation conversation;
	/**
	 * Adapter displaying the messages in the conversation
	 */
	private CommentsAdapter adapter;
	private ListView list;
	/**
	 * Draft of the reply we're writing for this conversation
	 */
	private String replyDraft = "";
	/**
	 * Save the scroll position to return too instead of jumping to the
	 * last post when restoring from an orientation change or such
	 */
	private Parcelable scrollState;
	/**
	 * Callback to send the conversation changes bundle back too
	 */
	private ConversationChangesPasser changesPasser;

	/**
	 * Create a conversation fragment displaying the messages in the
	 * desired conversation
	 *
	 * @param id id of conversation to view
	 */
	public static ConversationFragment newInstance(int id){
		ConversationFragment f = new ConversationFragment();
		Bundle args = new Bundle();
		args.putInt(CONVERSATION, id);
		f.setArguments(args);
		return f;
	}

	public ConversationFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			changesPasser = (ConversationChangesPasser)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ConversationChangesPasser");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null){
			replyDraft = savedInstanceState.getString(ReplyDialogFragment.DRAFT);
			scrollState = savedInstanceState.getParcelable(SCROLL_STATE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new CommentsAdapter(getActivity());
		list.setAdapter(adapter);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(ReplyDialogFragment.DRAFT, replyDraft);
		outState.putParcelable(SCROLL_STATE, list.onSaveInstanceState());
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public void quote(String quote){
		replyDraft += quote;
		showReplyDialog();
	}

	@Override
	public void post(String message, String subject){
		replyDraft = "";
		new PostReplyTask().execute(message);
	}

	@Override
	public void saveDraft(String message, String subject){
		replyDraft = message;
	}

	@Override
	public void manageConversation(int convId, boolean sticky, boolean unread, boolean delete){
		Bundle changes = new Bundle();
		changes.putInt(ConversationChangesPasser.CONVERSATION, convId);
		changes.putBoolean(ConversationChangesPasser.STICKY, sticky);
		changes.putBoolean(ConversationChangesPasser.UNREAD, unread);
		changes.putBoolean(ConversationChangesPasser.DELETED, delete);
		changesPasser.setChanges(changes);

		conversation.getResponse().setSticky(sticky);
		new ManageConversationTask(convId).execute(sticky, unread, delete);
	}

	@Override
	public void discard(){
		replyDraft = "";
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.conversation, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			//Show the reply dialog so the user can write their post
			case R.id.action_reply:
				showReplyDialog();
				return true;
			case R.id.action_manage:
				ManageConversationDialog dialog = ManageConversationDialog.newInstance(conversation.getResponse().getConvId().intValue(),
					conversation.getResponse().isSticky(), false);
				dialog.setTargetFragment(this, 0);
				dialog.show(getFragmentManager(), "dialog");
				return true;
			default:
				break;
		}
		return false;
	}

	/**
	 * Display the compose reply dialog so the user can write their response
	 */
	private void showReplyDialog(){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null){
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		ReplyDialogFragment reply = ReplyDialogFragment.newInstance(replyDraft);
		reply.setTargetFragment(this, 0);
		reply.show(ft, "dialog");
	}

	@Override
	public Loader<Conversation> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new ConversationAsyncLoader(getActivity(), getArguments());
	}

	@Override
	public void onLoadFinished(Loader<Conversation> loader, Conversation data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || data.getResponse() == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load conversation", Toast.LENGTH_LONG).show();
		}
		else {
			conversation = data;
			((SetTitleCallback)getActivity()).setTitle(conversation.getResponse().getSubject());
			if (adapter.isEmpty()){
				adapter.addAll(data.getResponse().getMessages());
				adapter.notifyDataSetChanged();
				//If we've got a saved scroll state use that, otherwise jump
				//to the last post in the conversation since it's our first time here
				if (scrollState != null){
					list.onRestoreInstanceState(scrollState);
				}
				else {
					list.setSelection(list.getCount() - 1);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Conversation> loader){
		conversation = null;
		adapter.clear();
		adapter.notifyDataSetChanged();
	}

	private class PostReplyTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params){
			return conversation.reply(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (!status){
				Toast.makeText(getActivity(), "Could not post reply", Toast.LENGTH_LONG).show();
			}
			else {
				//Reload the new posts to show
				getLoaderManager().destroyLoader(0);
				getLoaderManager().initLoader(0, getArguments(), ConversationFragment.this);
			}
		}
	}

	/**
	 * Manage the conversation in the background. Doesn't handle deletion as it doesn't
	 * make sense to keep viewing the conversation if we delete it. Instead we tell
	 * the inbox to delete the message and go back
	 */
	private class ManageConversationTask extends AsyncTask<Boolean, Void, Boolean> {
		private boolean delete;
		private int convId;

		public ManageConversationTask(int convId){
			this.convId = convId;
		}

		/**
		 * @param params {sticky, mark unread, delete}, true to set the corresponding value to true.
		 */
		@Override
		protected Boolean doInBackground(Boolean... params){
			delete = params[2];
			return Conversation.manage(convId, params[0], params[1], params[2]);
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (!status){
				Toast.makeText(getActivity(), "Could not update conversation", Toast.LENGTH_LONG).show();
			}
			else if (isAdded()){
				if (delete){
					Toast.makeText(getActivity(), "Conversation deleted", Toast.LENGTH_SHORT).show();
					getActivity().onBackPressed();
				}
				else {
					Toast.makeText(getActivity(), "Conversation updated", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
