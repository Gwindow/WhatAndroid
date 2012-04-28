package what.torrents.artist;

import what.gui.ImageLoader;
import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Gwindow
 * 
 */
public class ArtistActivity extends MyActivity {
	private TextView artistTitle;
	private ImageView artistImage;
	private WebView artistInfo;
	private ProgressDialog dialog;
	private Bitmap bmp;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.artist, false);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		artistTitle = (TextView) this.findViewById(R.id.artistTitle);
		artistImage = (ImageView) this.findViewById(R.id.artistImage);
		artistInfo = (WebView) this.findViewById(R.id.artistInfo);

	}

	@Override
	public void prepare() {
		new PopulateLayout().execute();
	}

	public void openSimilar(View v) {
		Bundle b = new Bundle();
		intent = new Intent(ArtistActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "artist_similiar");
		intent.putExtras(b);
		startActivity(intent);
	}

	public void openTags(View v) {
		Bundle b = new Bundle();
		intent = new Intent(ArtistActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "artist_tags");
		intent.putExtras(b);
		startActivity(intent);
	}

	public void openStats(View v) {
		intent = new Intent(ArtistActivity.this, what.torrents.artist.ArtistStatsActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		try {
			bmp.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private class PopulateLayout extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			// dialog = new ProgressDialog(ArtistActivity.this);
			// dialog.setIndeterminate(true);
			// dialog.setMessage("Loading...");
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String url = ArtistTabActivity.getArtist().getResponse().getImage();
			if (url.length() > 0) {
				try {
					bmp = ImageLoader.loadBitmap(url);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else
				return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (ArtistTabActivity.getArtist().getStatus()) {
				artistTitle.setText(ArtistTabActivity.getArtist().getResponse().getName());
				String body = ArtistTabActivity.getArtist().getResponse().getBody();
				if (body.length() > 0) {
					artistInfo.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
					artistInfo.getSettings().setSupportZoom(true);
					artistInfo.setVerticalScrollBarEnabled(true);
					artistInfo.setVerticalScrollbarOverlay(true);

					artistInfo.loadData(body, "text/html", "utf-8");
					artistInfo.setBackgroundColor(0);
					artistInfo.setBackgroundResource(R.drawable.color_transparent_white);
					artistInfo.setVisibility(WebView.VISIBLE);
				}
				if (status == true) {
					artistImage.setImageBitmap(bmp);
				} else {
					artistImage.setImageResource(R.drawable.noartwork);
				}
			}
			unlockScreenRotation();
			// dialog.dismiss();
		}
	}
}
