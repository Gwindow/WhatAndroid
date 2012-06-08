package what.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import what.gui.ImageLoader;
import what.gui.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import api.util.Triple;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 6, 2012 3:55:05 PM
 */
public class CoverArtFragment extends SherlockFragment implements OnClickListener {
	private static final int IMAGE_WIDTH = 50;
	private static final int BUFFER = 5;
	private static final int IMAGE_TAG = 0;
	private Triple<String, String, Integer>[] images;
	// url, imageview
	private HashMap<String, ImageView> imageMap;
	// id, bitmap
	private HashMap<Integer, Bitmap> bitmapMap;
	private HashMap<Integer, String> urlMap;
	private LinearLayout scrollLayout;
	private DisplayMetrics metrics;

	/**
	 * 
	 * @param images
	 *            A: url B: name C: id
	 */
	public CoverArtFragment(Triple<String, String, Integer>[] images) {
		this.images = images;
		bitmapMap = new HashMap<Integer, Bitmap>();
		urlMap = new HashMap<Integer, String>();
		metrics = new DisplayMetrics();
		getSherlockActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		fillMaps();
	}

	private void fillMaps() {
		for (int i = 0; i < images.length; i++) {
			urlMap.put(images[i].getC(), images[i].getA());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populate(view, inflater);
		return view;
	}

	private void populate(View view, LayoutInflater inflater) {
		int screen_width = metrics.widthPixels;
		int items_in_row = (int) Math.floor(screen_width / (IMAGE_WIDTH + BUFFER));
		int rows = (int) Math.ceil(images.length / items_in_row);
		int current_item = 0;
		for (int i = 0; i < rows; i++) {
			ArrayList<Triple<String, String, Integer>> list = new ArrayList<Triple<String, String, Integer>>();
			for (int j = 0; j < items_in_row; j++) {
				if (current_item < images.length) {
					list.add(images[current_item]);
					current_item++;
				}
			}
			if (!list.isEmpty()) {
				addRow(list);
			}
		}
	}

	private void addRow(ArrayList<Triple<String, String, Integer>> list) {
		LinearLayout row_layout = new LinearLayout(getSherlockActivity());
		row_layout.setOrientation(LinearLayout.HORIZONTAL);
		row_layout.setGravity(Gravity.CENTER);
		for (int i = 0; i < list.size(); i++) {
			ImageView image_view = new ImageView(getSherlockActivity());
			image_view.setScaleType(ScaleType.CENTER_INSIDE);
			image_view.setId(list.get(i).getC());
			image_view.setTag(IMAGE_TAG);
			image_view.setOnClickListener(this);
			new LoadImage(list.get(i).getC(), image_view).execute();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private class LoadImage extends AsyncTask<Void, Void, Boolean> {
		private final ImageView imageView;
		private Bitmap bitmap;
		private final int id;

		public LoadImage(int id, ImageView imageView) {
			this.id = id;
			this.imageView = imageView;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean status = false;
			String url = urlMap.get(id);
			if (url.length() > 0) {
				try {
					bitmap = ImageLoader.loadBitmap(url);
					status = true;
				} catch (Exception e) {
				}
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (!status) {
				bitmap = (BitmapFactory.decodeResource(getResources(), R.drawable.noartwork));
			}
			bitmapMap.put(id, bitmap);
			imageView.setImageBitmap(bitmap);
		}
	}
}
