package what.torrents.artist;

import java.net.URL;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
		setContentView(R.layout.artist);
		artistTitle = (TextView) this.findViewById(R.id.artistTitle);
		artistImage = (ImageView) this.findViewById(R.id.artistImage);
		// artistImage.setMaxWidth(this.getWidth() / 3);
		// artistImage.setMaxHeight(this.getHeight() / 2);
		artistInfo = (WebView) this.findViewById(R.id.artistInfo);
		// artistInfo.setMovementMethod(new ScrollingMovementMethod());

		new PopulateLayout().execute();

	}

	public void openSimilar(View v) {
		Bundle b = new Bundle();
		intent = new Intent(ArtistActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "artist_similiar");
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void openTags(View v) {
		Bundle b = new Bundle();
		intent = new Intent(ArtistActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "artist_tags");
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void openStats(View v) {
		intent = new Intent(ArtistActivity.this, what.torrents.artist.ArtistStatsActivity.class);
		startActivityForResult(intent, 0);
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
			dialog = new ProgressDialog(ArtistActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			URL url;
			try {
				url = new URL(ArtistTabActivity.getArtist().getResponse().getImage());
				bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (ArtistTabActivity.getArtist().getStatus()) {
				artistTitle.setText(ArtistTabActivity.getArtist().getResponse().getName());
				String body = ArtistTabActivity.getArtist().getResponse().getBody();
				if (body.length() > 0) {
					artistInfo.loadData(body, "text/html", "utf-8");
					artistInfo.setVisibility(TextView.VISIBLE);
				}
				if (status == true) {
					artistImage.setImageBitmap(bmp);
				} else {
					artistImage.setImageResource(R.drawable.dne);
				}
			}
			dialog.dismiss();
		}
	}
}
