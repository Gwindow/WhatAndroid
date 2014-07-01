package what.whatandroid.subscriptions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import api.soup.MySoup;
import api.subscriptions.Subscriptions;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;

/**
 * Fragment to display the listing of user's subscriptions grouped by forum
 */
public class SubscriptionsFragment extends Fragment implements OnLoggedInCallback,
	LoaderManager.LoaderCallbacks<Subscriptions> {
	public static final String SHOW_ALL = "what.whatandroid.subscriptions.SHOW_ALL";
	/**
	 * List adapter displaying the subscription information
	 */
	private SubscriptionsAdapter adapter;
	private ProgressBar loadingIndicator;
	private TextView noContent;
	/**
	 * Used to track what state of subscriptions we're showing, all or just
	 * the unread ones
	 */
	private boolean showAll = false;
	private MenuItem showAllMenu;

	public SubscriptionsFragment(){
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null){
			showAll = savedInstanceState.getBoolean(SHOW_ALL);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		updateMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView) view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar) view.findViewById(R.id.loading_indicator);
		noContent = (TextView) view.findViewById(R.id.no_content_notice);
		adapter = new SubscriptionsAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		noContent.setText("No subscriptions");

		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean(SHOW_ALL, showAll);
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			Bundle args = new Bundle();
			args.putBoolean(SHOW_ALL, showAll);
			getLoaderManager().initLoader(0, args, this);
		}
	}

	@Override
	public Loader<Subscriptions> onCreateLoader(int id, Bundle args){
		loadingIndicator.setVisibility(View.VISIBLE);
		return new SubscriptionsAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Subscriptions> loader, Subscriptions data){
		loadingIndicator.setVisibility(View.GONE);
		if (data == null || !data.getStatus()){
			Toast.makeText(getActivity(), "Could not load subscriptions", Toast.LENGTH_LONG).show();
		}
		else if (adapter.isEmpty()){
			adapter.addSubscriptions(data);
			adapter.notifyDataSetChanged();
			//If there's no subscriptions to show display the no content notice
			if (adapter.isEmpty()){
				noContent.setVisibility(View.VISIBLE);
			}
			else {
				noContent.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Subscriptions> loader){
		adapter.clear();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.subscriptions, menu);
		showAllMenu = menu.findItem(R.id.action_show_all);
		updateMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_show_all){
			showAll = !showAll;
			Bundle args = new Bundle();
			args.putBoolean(SHOW_ALL, showAll);
			adapter.clear();
			getLoaderManager().restartLoader(0, args, this);
			updateMenu();
			return true;
		}
		return false;
	}

	/**
	 * Update the "show all" menu item to show the proper text for what
	 * action will be execute, eg. "show all" or "show unread"
	 */
	private void updateMenu(){
		if (showAllMenu != null){
			if (showAll){
				showAllMenu.setTitle("Show Unread");
			}
			else {
				showAllMenu.setTitle("Show All");
			}
		}
	}
}
