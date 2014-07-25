package what.whatandroid.inbox;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Collections;

import api.inbox.inbox.Inbox;
import api.inbox.inbox.Message;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment for displaying a list of conversations on
 * some page of the user's inbox
 */
public class InboxListFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Inbox> {
	public static final String PAGE = "what.whatandroid.inboxlistfragment.PAGE";
	private static final String SCROLL_STATE = "what.whatandroid.inboxlistfragment.SCROLL_STATE";

	/**
	 * Listener to alert when we're finished loading this page
	 */
	private LoadingListener<Inbox> listener;
	/**
	 * Conversation changes passer to query after loading if there's
	 * been some changes made so we can update our view
	 */
	private ConversationChangesPasser changesPasser;

	private ProgressBar loadingIndicator;
	private ListView list;
	private InboxListAdapter adapter;
	private Parcelable scrollState;

	/**
	 * Create a fragment displaying the list of conversations in some
	 * page of the users inbox
	 *
	 * @param page page of messages to show
	 */
	public static InboxListFragment newInstance(int page){
		InboxListFragment f = new InboxListFragment();
		Bundle args = new Bundle();
		args.putInt(PAGE, page);
		f.setArguments(args);
		return f;
	}

	public InboxListFragment(){
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
		if (savedInstanceState != null){
			scrollState = savedInstanceState.getParcelable(SCROLL_STATE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new InboxListAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (MySoup.isLoggedIn()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if (list != null){
			outState.putParcelable(SCROLL_STATE, list.onSaveInstanceState());
		}
	}

	public void setListener(LoadingListener<Inbox> listener){
		this.listener = listener;
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public Loader<Inbox> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new InboxAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Inbox> loader, Inbox data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || data.getResponse() == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load page", Toast.LENGTH_LONG).show();
		}
		else {
			if (adapter.isEmpty()){
				//Check if any conversation changes were made to the conversations we're showing
				applyConversationChanges(data);
				adapter.addAll(data.getResponse().getMessages());
				adapter.notifyDataSetChanged();
				if (listener != null){
					listener.onLoadingComplete(data);
				}
				if (scrollState != null){
					list.onRestoreInstanceState(scrollState);
				}
			}
		}
	}

	/**
	 * Check if the conversation changes are being applied to
	 * one of the conversations we're displaying and apply them if so
	 */
	public void applyConversationChanges(Inbox data){
		if (changesPasser.hasChanges()){
			Bundle changes = changesPasser.getChanges();
			int convId = changes.getInt(ConversationChangesPasser.CONVERSATION);
			for (int i = 0; i < data.getResponse().getMessages().size(); ++i){
				Message m = data.getResponse().getMessages().get(i);
				if (m.getConvId().intValue() == convId){
					if (changes.getBoolean(ConversationChangesPasser.DELETED)){
						data.getResponse().getMessages().remove(i);
						//Notify the passer that we've consumed the changes
						changesPasser.consumeChanges();
						return;
					}
					m.setSticky(changes.getBoolean(ConversationChangesPasser.STICKY));
					m.setUnread(changes.getBoolean(ConversationChangesPasser.UNREAD));
					//Re-sort the messages to put sticky posts at the top
					Collections.sort(data.getResponse().getMessages(), new Message.StickyMessageComparator());
					//Notify the passer that we've consumed the changes
					changesPasser.consumeChanges();
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Inbox> loader){
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
