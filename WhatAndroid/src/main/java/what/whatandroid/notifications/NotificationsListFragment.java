package what.whatandroid.notifications;

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
import android.widget.TextView;
import android.widget.Toast;

import api.notifications.Notifications;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Displays some page of the users notifications
 */
public class NotificationsListFragment extends Fragment implements OnLoggedInCallback, LoaderManager.LoaderCallbacks<Notifications> {
	public static final String PAGE = "what.whatandroid.NOTIFICATIONS_PAGE",
		SCROLL_STATE = "what.whatandroid.notificationslistfragment.SCROLL_STATE";
	private ListView list;
	private Parcelable scrollState;
	private NotificationsListAdapter adapter;
	private ProgressBar loadingIndicator;
	private TextView noContent;
	private LoadingListener<Notifications> listener;

	public static NotificationsListFragment newInstance(int page){
		NotificationsListFragment f = new NotificationsListFragment();
		Bundle args = new Bundle();
		args.putInt(PAGE, page);
		f.setArguments(args);
		return f;
	}

	public NotificationsListFragment(){
		//Required empty ctor
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
		list = (ListView) view.findViewById(R.id.list);
		noContent = (TextView)view.findViewById(R.id.no_content_notice);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new NotificationsListAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		noContent.setText("No notifications");
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

	public void setLoadingListener(LoadingListener<Notifications> listener){
		this.listener = listener;
	}

	public void clearNotifications(){
		getLoaderManager().destroyLoader(0);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public Loader<Notifications> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new NotificationsAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Notifications> loader, Notifications data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load notifications", Toast.LENGTH_LONG).show();
		}
		else if (adapter.isEmpty()){
			adapter.addAll(data.getResponse().getResults());
			adapter.notifyDataSetChanged();
			if (data.getResponse().getResults().isEmpty()){
				noContent.setVisibility(View.VISIBLE);
			}
			else if (scrollState != null){
				list.onRestoreInstanceState(scrollState);
			}
			if (listener != null){
				listener.onLoadingComplete(data);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Notifications> loader){
		adapter.clear();
		adapter.notifyDataSetChanged();
		noContent.setVisibility(View.VISIBLE);
	}
}
