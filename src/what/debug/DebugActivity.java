package what.debug;

import what.gui.ActivityNames;
import what.gui.MyActivity2;
import what.gui.R;
import android.os.Bundle;
import android.webkit.WebView;
import api.son.MySon;

/**
 * 
 *
 */
public class DebugActivity extends MyActivity2 {
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setActivityName(ActivityNames.DEBUG);
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.debug, false);
	}

	@Override
	public void init() {
	}

	@Override
	public void load() {
		webView = (WebView) this.findViewById(R.id.webView);
	}

	@Override
	public void prepare() {
		setActionBarTitle("Debug");
		try {
			webView.loadData(MySon.getDebugString(), "text/html", "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
