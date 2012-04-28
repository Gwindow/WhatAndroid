package what.sherlockbot;

import what.gui.MyActivity;
import what.gui.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Toast;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

public class SherlockBotActivity extends MyActivity {
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.sherlock, true);
	}

	@Override
	public void init() {
	}

	@Override
	public void load() {
		webView = (WebView) this.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setLoadWithOverviewMode(true);
	}

	@Override
	public void prepare() {
		enableGestures(false);
		webView.loadUrl("http://sherlock.whatbarco.de/");
	}

	public void add(View v) {
		final AlertDialog alert = new AlertDialog.Builder(SherlockBotActivity.this).create();
		alert.setMessage("Do you want to be added to Sherlock? Your stats on What.CD will be tracked. Make sure your paranoia is disabled in your user profile. After being added you can login with your username and the password What?CD");
		alert.setButton(AlertDialog.BUTTON1, "Add me to Sherlock", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					MySoup.scrapeOther("http://sherlock.whatbarco.de/adduser.php?username=" + MySoup.getUsername() + "&userid="
							+ MySoup.getUserId());
					Toast.makeText(SherlockBotActivity.this, "You have been added", Toast.LENGTH_SHORT).show();
				} catch (CouldNotLoadException e) {
					Toast.makeText(SherlockBotActivity.this, "Error, could not add", Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setButton(AlertDialog.BUTTON2, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alert.cancel();
			}
		});

		alert.setCancelable(true);
		alert.show();
	}

	public void help(View v) {
		final AlertDialog alert = new AlertDialog.Builder(SherlockBotActivity.this).create();
		alert.setMessage("If you have been added to Sherlock you can login with your username and the password What?CD. Stats are refreshed at the top of each hour.");
		alert.setButton(AlertDialog.BUTTON1, "Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alert.cancel();
			}
		});
		alert.setCancelable(true);
		alert.show();
	}
}
