package what.torrents;

import what.gui.ImageLoader;
import what.gui.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Gwindow
 * @since Jun 1, 2012 5:40:10 PM
 */
public class ArtFragment extends SherlockFragment {
	private ProgressBar progressBar;
	private ImageView artImageView;
	private static Bitmap artBitmap;

	private String url;

	/**
	 * Instantiates a new torrent group fragment.
	 * 
	 * @param title
	 *            the title
	 * @param torrentGroup
	 */
	public ArtFragment(String url) {
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.art_fragment, container, false);
		artImageView = (ImageView) view.findViewById(R.id.art);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		if (artBitmap == null) {
			Toast.makeText(this.getSherlockActivity(), "Loading image", Toast.LENGTH_SHORT).show();
			new LoadImage().execute(url);
		} else {
			progressBar.setVisibility(View.GONE);
			artImageView.setVisibility(View.VISIBLE);
			artImageView.setImageBitmap(artBitmap);
		}
		return view;
	}

	private class LoadImage extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean status = false;
			String url = params[0];
			if (url.length() > 0) {
				try {
					artBitmap = ImageLoader.loadBitmap(url);
					status = true;
				} catch (Exception e) {
				}
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			progressBar.setVisibility(View.GONE);
			artImageView.setVisibility(View.VISIBLE);
			if (!status) {
				artBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dne);
			}
			artImageView.setImageBitmap(artBitmap);
		}
	}

}
