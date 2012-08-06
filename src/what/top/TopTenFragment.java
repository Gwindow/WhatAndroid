package what.top;

import java.util.List;

import what.gui.BundleKeys;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.torrents.torrents.DownloadDialog;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.son.MySon;
import api.top.Response;
import api.top.Top;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jul 15, 2012 12:20:25 PM
 */
public class TopTenFragment extends SherlockFragment implements OnClickListener, OnLongClickListener {
	private static final int DOWNLOAD_TAG = 0;
	private static final int GROUP_TAG = 1;
	private Top top;
	private String tag;
	private LinearLayout scrollLayout;
	private MyScrollView scrollView;

	/**
	 * @param response
	 * @param snatchedTag
	 */
	public TopTenFragment(Top top, String tag) {
		this.tag = tag;
		this.top = top;
	}

	public TopTenFragment() {
		super();
	}

	public static SherlockFragment newInstance(Top top, String tag) {
		return new TopTenFragment(top, tag);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null)) {
			top = (Top) MySon.toObjectFromString(savedInstanceState.getString("top"), Top.class);
			tag = savedInstanceState.getString("tag");
			Log.d("fragment", top.getResponse().get(0).getCaption());
		} else {
			Log.d("fragment", "null");
		}
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		// populateMusic();
		return view;
	}

	private void populateMusic() {
		List<Response> response = top.getResponse();
		for (int j = 0; j < response.size(); j++) {
			if (response.get(j).getTag().equals(tag)) {
				for (int i = 0; i < response.get(j).getResults().size(); i++) {
					LinearLayout formats_torrent_layout =
							(LinearLayout) View.inflate(getSherlockActivity(), R.layout.formats_torrent, null);
					TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);

					String format_string = "";
					if (response.get(j).getResults().get(i).getArtist() != null
							&& !response.get(j).getResults().get(i).getArtist().equalsIgnoreCase("false")) {
						format_string += response.get(j).getResults().get(i).getArtist() + " - ";
					}
					format_string += response.get(j).getResults().get(i).getGroupName();
					if (response.get(j).getResults().get(i).getGroupYear() != null
							&& response.get(j).getResults().get(i).getGroupYear().intValue() != 0) {
						format_string += " [" + response.get(j).getResults().get(i).getGroupYear() + "]";
					}

					format.setText(format_string);
					format.setOnClickListener(this);
					format.setOnLongClickListener(this);
					format.setId(GROUP_TAG);

					Object[] array = new Object[6];
					array[0] = response.get(j).getResults().get(i).getGroupId();
					array[1] = response.get(j).getResults().get(i).getDownloadLink();
					array[2] = response.get(j).getResults().get(i).getData();
					array[3] = response.get(j).getResults().get(i).getSnatched();
					array[4] = response.get(j).getResults().get(i).getSeeders();
					array[5] = response.get(j).getResults().get(i).getLeechers();
					format.setTag(array);

					scrollLayout.addView(formats_torrent_layout);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == GROUP_TAG) {
			Object[] array = (Object[]) v.getTag();
			Intent intent = new Intent(getActivity(), TorrentGroupActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(BundleKeys.TORRENT_GROUP_ID, ((Number) array[0]).intValue());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Object[] array = (Object[]) v.getTag();
		new DownloadDialog(getSherlockActivity(), (Number) array[0], (String) array[1], (Number) array[2], (Number) array[3],
				(Number) array[4], (Number) array[5]);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("top", MySon.toJson(top, Top.class));
		outState.putString("tag", tag);

		Log.d("fragment", "saved");
	}
}
