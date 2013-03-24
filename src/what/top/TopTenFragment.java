package what.top;

import java.util.List;

import api.top.Results;
import what.gui.BundleKeys;
import what.gui.DownloadDialog;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Intent;
import android.os.Bundle;
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
	private static final int GROUP_TAG = 0;
	private Top top;
	private String tag;
	private LinearLayout scrollLayout;
	private MyScrollView scrollView;

	/**
	 * @param
	 * @param
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
        //TODO: should we call super.onCreateView()?
		if ((savedInstanceState != null)) {
			top = (Top) MySon.toObjectFromString(savedInstanceState.getString(BundleKeys.SAVED_JSON), Top.class);
			tag = savedInstanceState.getString(BundleKeys.TAG);
		}
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populateMusic();
		return view;
	}

	private void populateMusic() {
		List<Response> response = top.getResponse();
        for (Response resp : response){
            if (resp.getTag().equals(tag)){
                for (Results res : resp.getResults()){
                    LinearLayout formats_torrent_layout =
                            (LinearLayout) View.inflate(getSherlockActivity(), R.layout.formats_torrent, null);
                    TextView format = (TextView) formats_torrent_layout.findViewById(R.id.format);

                    String format_string = "";
                    if (res.getArtist() != null && !res.getArtist().equalsIgnoreCase("false")) {
                        format_string += res.getArtist() + " - ";
                    }
                    format_string += res.getGroupName();
                    if (res.getGroupYear() != null && res.getGroupYear().intValue() != 0) {
                        format_string += " [" + res.getGroupYear() + "]" + "[" + res.getEncoding() + "]";
                    }

                    format.setText(format_string);
                    format.setOnClickListener(this);
                    format.setOnLongClickListener(this);
                    format.setId(GROUP_TAG);

                    Object[] array = new Object[7];
                    array[0] = res.getTorrentId();
                    array[1] = res.getDownloadLink();
                    //TODO: Data is not the size of the torrent, how can I get the size?
                    //Temp fix: data/snatched is within +/-5mb of the torrent size, or permanent fix if
                    //we can't get the torrent size added to the top 10 api response
                    array[2] = res.getData().doubleValue() / res.getSnatched().doubleValue();
                    array[3] = res.getSnatched();
                    array[4] = res.getSeeders();
                    array[5] = res.getLeechers();
                    array[6] = res.getGroupName();
                    format.setTag(array);

                    scrollLayout.addView(formats_torrent_layout);
                }
                //We found the tag we're interested in and populated with it, so now we can break
                break;
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
				(Number) array[4], (Number) array[5], (String)array[6]);
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
		outState.putString(BundleKeys.SAVED_JSON, MySon.toJson(top, Top.class));
		outState.putString(BundleKeys.TAG, tag);

	}
}
