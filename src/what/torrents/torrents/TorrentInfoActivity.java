package what.torrents.torrents;

import java.net.URL;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import api.torrents.torrents.TorrentGroup;

public class TorrentInfoActivity extends MyActivity {
	private TextView torrentTitle;
	private ImageView torrentImage;
	private WebView torrentInfo;
	private Bitmap bmp;
	private TorrentGroup torrentGroup;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.torrentinfo, false);

		torrentTitle = (TextView) this.findViewById(R.id.torrentTitle);
		torrentImage = (ImageView) this.findViewById(R.id.torrentImage);
		torrentInfo = (WebView) this.findViewById(R.id.torrentInfo);
		torrentGroup = TorrentTabActivity.getTorrentGroup();

		new PopulateLayout().execute();
	}

	// TODO add in php, disabled at the moment
	public void openTags(View v) {
		Bundle b = new Bundle();
		intent = new Intent(TorrentInfoActivity.this, what.torrents.ListActivity.class);
		b.putString("type", "torrent_tags");
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}

	public void openStats(View v) {
		intent = new Intent(TorrentInfoActivity.this, what.torrents.torrents.TorrentStatsActivity.class);
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
			// dialog = new ProgressDialog(TorrentInfoActivity.this);
			// dialog.setIndeterminate(true);
			// dialog.setMessage("Loading...");
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			URL url;
			String u = torrentGroup.getResponse().getGroup().getWikiImage();
			if (u.length() > 0) {
				try {
					url = new URL(torrentGroup.getResponse().getGroup().getWikiImage());
					bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
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
			if (torrentGroup.getStatus()) {
				if (true) {
					if (torrentGroup.hasFreeLeech()) {
						torrentTitle.setText("Freeleech! " + torrentGroup.getResponse().getGroup().getName());
						torrentTitle.setTextColor(Color.YELLOW);
					} else {
						torrentTitle.setText(torrentGroup.getResponse().getGroup().getName());
					}
					String body = torrentGroup.getResponse().getGroup().getWikiBody();
					if (body.length() > 0) {
						torrentInfo.loadData(body, "text/html", "utf-8");
						torrentInfo.setVisibility(WebView.VISIBLE);
					}
					if (status == true) {
						torrentImage.setImageBitmap(bmp);
					} else {
						torrentImage.setImageResource(R.drawable.noartwork);
					}
				}
			}
			unlockScreenRotation();
		}
	}
}
