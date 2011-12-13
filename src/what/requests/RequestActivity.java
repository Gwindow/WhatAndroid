package what.requests;

import java.net.URL;
import java.text.DecimalFormat;

import what.gui.MyActivity;
import what.gui.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestActivity extends MyActivity {
	private TextView requestTitle, bounty, bitrate, formats, media;
	private ImageView requestImage;
	private WebView requestInfo;
	private Bitmap bmp;
	private Intent intent;
	private api.requests.Request request = RequestTabActivity.getRequest();
	private DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request);
		requestTitle = (TextView) this.findViewById(R.id.requestTitle);
		bounty = (TextView) this.findViewById(R.id.bounty);
		bitrate = (TextView) this.findViewById(R.id.bitrate);
		formats = (TextView) this.findViewById(R.id.formats);
		media = (TextView) this.findViewById(R.id.media);
		requestImage = (ImageView) this.findViewById(R.id.requestImage);
		requestInfo = (WebView) this.findViewById(R.id.requestInfo);
		new LoadImage().execute();
		populateLayout();
	}

	private void populateLayout() {
		requestTitle.setText(request.getResponse().getCategoryName() + " - " + request.getResponse().getTitle() + " ["
				+ request.getResponse().getYear().intValue() + "]");

		bounty.setText("Bounty: " + toGBString(request.getResponse().getTotalBounty().doubleValue()) + " GB");
		bitrate.setText("Bitrate: " + request.getResponse().getBitrateList());
		formats.setText("Formats: " + request.getResponse().getFormatList());
		media.setText("Media: " + request.getResponse().getMediaList());

		String body = request.getResponse().getDescription();
		if (body.length() > 0) {
			requestInfo.loadData(body, "text/html", "utf-8");
			requestInfo.setVisibility(WebView.VISIBLE);
		}
	}

	private String toGBString(Double s) {
		double d = s / Math.pow(1024, 3);
		return df.format(d);
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
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			URL url;
			String s = request.getResponse().getImage();
			if (s.length() > 0) {
				try {
					url = new URL(s);
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
			if (request.getStatus()) {
				if (status == true) {
					requestImage.setImageBitmap(bmp);
				} else {
					requestImage.setImageResource(R.drawable.noartwork);
				}
			}
			unlockScreenRotation();
		}
	}

}
