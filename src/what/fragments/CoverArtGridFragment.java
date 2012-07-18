package what.fragments;

import java.io.IOException;
import java.util.List;

import what.gui.BundleKeys;
import what.gui.ImageLoader;
import what.gui.R;
import what.torrents.torrents.TorrentGroupActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import api.bookmarks.Torrents;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:55:05 PM
 */
public class CoverArtGridFragment extends SherlockFragment {
	private GridView gridView;
	private final List<Torrents> torrents;

	public CoverArtGridFragment(List<Torrents> torrents) {
		this.torrents = torrents;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cover_art, container, false);
		gridView = (GridView) view.findViewById(R.id.gridView);
		gridView.setAdapter(new ImageAdapter(getSherlockActivity()));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				openTorrentGroup(torrents.get(position).getId().intValue());
			}
		});

		return view;
	}

	private void openTorrentGroup(int id) {
		Intent intent = new Intent(getSherlockActivity(), TorrentGroupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.TORRENT_GROUP_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return torrents.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(12, 12, 12, 12);
				imageView.setTag(torrents.get(position).getImage());
				imageView.setImageResource(R.drawable.noartwork);
				new DownloadImage().execute(imageView);
			} else {
				imageView = (ImageView) convertView;
			}
			return imageView;

		}
	}

	private class DownloadImage extends AsyncTask<ImageView, Void, Bitmap> {
		ImageView imageView = null;

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
			this.imageView = imageViews[0];
			try {
				return ImageLoader.loadBitmap((String) imageView.getTag());
			} catch (IOException e) {
				return BitmapFactory.decodeResource(CoverArtGridFragment.this.getResources(), R.drawable.dne);
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
	}
}
