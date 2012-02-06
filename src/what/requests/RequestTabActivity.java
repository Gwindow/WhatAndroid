package what.requests;

import what.gui.MyTabActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TabHost;
import android.widget.Toast;
import api.requests.Request;

public class RequestTabActivity extends MyTabActivity {
	private Resources res; // Resource object to get Drawables
	private TabHost tabHost;// The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	private Intent intent; // Reusable Intent for each tab
	private ProgressDialog dialog;
	private static Request request;
	private static int requestId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.tabs, true);

		getBundle();
		new LoadRequest().execute();

	}

	private void createTabs() {
		res = getResources();
		tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(RequestTabActivity.this, RequestActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("request").setIndicator("Request", res.getDrawable(R.drawable.tab_request)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(RequestTabActivity.this, RequestDetailsActivity.class);
		spec = tabHost.newTabSpec("details").setIndicator("Details", res.getDrawable(R.drawable.tab_details)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	/** Get the bundle from the previous intent specifying the artist id */
	private void getBundle() {
		try {
			Bundle b = this.getIntent().getExtras();
			requestId = b.getInt("requestId");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if ((e2.getX() - e1.getX()) > 35) {
			try {
				tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((e2.getX() - e1.getX()) < -35) {
			try {
				tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private class LoadRequest extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(RequestTabActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			request = Request.requestFromId(requestId);
			return request.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				createTabs();
			}
			dialog.dismiss();
			if (status == false) {
				Toast.makeText(RequestTabActivity.this, "Could not load request", Toast.LENGTH_LONG).show();
			}
			unlockScreenRotation();
		}
	}

	/**
	 * @return the request
	 */
	public static Request getRequest() {
		return request;
	}

	/**
	 * @return the requestId
	 */
	public static int getRequestId() {
		return requestId;
	}

}
