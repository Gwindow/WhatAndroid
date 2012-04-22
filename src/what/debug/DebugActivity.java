package what.debug;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.webkit.WebView;
import api.son.MySon;

/**
 * 
 *
 */
public class DebugActivity extends MyActivity {
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.debug, false);
		enableGestures(false);
		webView = (WebView) this.findViewById(R.id.webView);
		webView.loadData(MySon.getDebugString(), "text/html", "utf-8");
	}
}
