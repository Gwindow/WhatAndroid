package what.whatandroid.inbox.conversation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
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
import what.whatandroid.comments.CommentsAdapter;

/**
 * Fragment for displaying the list of messages in a conversation
 */
public class ConversationFragment extends Fragment implements OnLoggedInCallback,
	AddQuoteCallback, LoaderManager.LoaderCallbacks<Conversation> {

	public static final String CONVERSATION = "what.whatandroid.conversationfragment.CONVERSATION";

	private ProgressBar loadingIndicator;
	/**
	 * Adapter displaying the messages in the conversation
	 */
	private CommentsAdapter adapter;
	private ListView list;

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
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public void quote(String quote){
		System.out.println("Quoting " + quote);
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
			if (adapter.isEmpty()){
				adapter.addAll(data.getResponse().getMessages());
				adapter.notifyDataSetChanged();
				//Jump to the most recent post in the conversation
				list.setSelection(list.getCount() - 1);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Conversation> loader){
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
