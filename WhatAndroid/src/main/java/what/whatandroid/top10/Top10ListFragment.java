package what.whatandroid.top10;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import api.top.Response;
import api.top.Torrent;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Displays a list of the top 10 torrents for some category
 */
public class Top10ListFragment extends Fragment implements LoadingListener<Response<Torrent>> {
	/**
	 * The adapter displaying the list of top torrents
	 */
	private Top10Adapter adapter;
	private ProgressBar loadingIndicator;
	/**
	 * The list of torrents being shown
	 */
	private List<Torrent> torrents;

	public Top10ListFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator);
		adapter = new Top10Adapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if (torrents != null){
			adapter.addAll(torrents);
			adapter.notifyDataSetChanged();
		}
		else {
			loadingIndicator.setVisibility(View.VISIBLE);
		}
		return view;
	}

	@Override
	public void onLoadingComplete(Response<Torrent> data){
		torrents = data.getResults();
		if (adapter != null){
			loadingIndicator.setVisibility(View.GONE);
			if (adapter.isEmpty()){
				adapter.addAll(torrents);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
