package what.torrents.torrents;

import what.gui.ImageLoader;
import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import api.torrents.torrents.TorrentGroup;

public class TorrentInfoActivity extends MyActivity {
	private static final String IMAGE_STATE_STRING = "Album Art";
	private static final String DESCRIPTION_STATE_STRING = "Description";
	private ViewFlipper viewFlipper;
	private TextView torrentTitle;
	private ImageView torrentImage;
	private Button flipViewButton;
	private WebView torrentInfo;
	private Bitmap bmp;
	private TorrentGroup torrentGroup;
	private Intent intent;
	// false is state 1, true is state 2
	private boolean viewFlipperState = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrentinfo, false);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		torrentTitle = (TextView) this.findViewById(R.id.torrentTitle);
		torrentImage = (ImageView) this.findViewById(R.id.torrentImage);
		torrentInfo = (WebView) this.findViewById(R.id.torrentInfo);
		viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		flipViewButton = (Button) this.findViewById(R.id.flipViewButton);
		torrentGroup = TorrentTabActivity.getTorrentGroup();
	}

	@Override
	public void prepare() {
		populateLayout();
		new LoadImage().execute();
	}

	private void populateLayout() {
		if (torrentGroup.getStatus()) {
			if (torrentGroup.hasFreeLeech()) {
				torrentTitle.setText("Freeleech! " + torrentGroup.getResponse().getGroup().getName());
				torrentTitle.setTextColor(Color.YELLOW);
			} else {
				torrentTitle.setText(torrentGroup.getResponse().getGroup().getName());
			}
			String body = torrentGroup.getResponse().getGroup().getWikiBody();
			torrentInfo.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
			torrentInfo.getSettings().setSupportZoom(true);
			torrentInfo.setVerticalScrollBarEnabled(true);
			torrentInfo.setVerticalScrollbarOverlay(true);
			torrentInfo.setBackgroundColor(0);
			torrentInfo.setBackgroundResource(R.drawable.color_transparent_white);
			if (body.length() > 0) {
				torrentInfo.loadData(body, "text/html", "utf-8");
			} else {
				torrentInfo.loadData("No description", "text/html", "utf-8");
			}
		}
	}

	public void flipView(View v) {
		if (viewFlipperState == true) {
			viewFlipper.showNext();
			flipViewButton.setText(DESCRIPTION_STATE_STRING);
			viewFlipperState = false;
		} else {
			viewFlipper.showPrevious();
			flipViewButton.setText(IMAGE_STATE_STRING);
			viewFlipperState = true;
		}
	}

	// TODO add in php, disabled at the moment
	public void openTags(View v) {
		Bundle b = new Bundle();
		intent = new Intent(TorrentInfoActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "torrent_tags");
		intent.putExtras(b);
		startActivity(intent);
	}

	public void openStats(View v) {
		intent = new Intent(TorrentInfoActivity.this, what.torrents.torrents.TorrentStatsActivity.class);
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

	private class LoadImage extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			// dialog = new ProgressDialog(TorrentInfoActivity.this);
			// dialog.setIndeterminate(true);
			// dialog.setMessage("Loading...");
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String url = torrentGroup.getResponse().getGroup().getWikiImage();
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
			// dialog.dismiss();
			if (status == true) {
				torrentImage.setImageBitmap(bmp);
			} else {
				torrentImage.setImageResource(R.drawable.noartwork);
			}
			unlockScreenRotation();
		}
	}
}
