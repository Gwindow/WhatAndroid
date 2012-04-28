package what.requests;

import what.gui.ImageLoader;
import what.gui.MyActivity;
import what.gui.R;
import android.graphics.Bitmap;
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
	private api.requests.Request request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.request, false);
	}

	@Override
	public void init() {
		request = RequestTabActivity.getRequest();
	}

	@Override
	public void load() {
		requestTitle = (TextView) this.findViewById(R.id.requestTitle);
		bounty = (TextView) this.findViewById(R.id.bounty);
		bitrate = (TextView) this.findViewById(R.id.bitrate);
		formats = (TextView) this.findViewById(R.id.formats);
		media = (TextView) this.findViewById(R.id.media);
		requestImage = (ImageView) this.findViewById(R.id.requestImage);
		requestInfo = (WebView) this.findViewById(R.id.requestInfo);
	}

	@Override
	public void prepare() {
		new LoadImage().execute();
		populateLayout();
	}

	private void populateLayout() {
		requestTitle.setText(request.getResponse().getCategoryName() + " - " + request.getResponse().getTitle());

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
			String s = request.getResponse().getImage();
			if (s.length() > 0) {
				try {
					bmp = ImageLoader.loadBitmap(s);
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
