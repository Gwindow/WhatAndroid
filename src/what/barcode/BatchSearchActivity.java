package what.barcode;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import what.gui.R;

/**
 * An activity for performing a search on the site of the barcodes stored in
 * the quick scanner barcode file
 * Although in the 1.x version of the app we simply call into the torrent search activity
 * I'd prefer later down the line to make the various searches into fragments
 * so that we can then tack on the search fragment and swipe between the upc list
 * and the selected search and selected results from the search
 */
public class BatchSearchActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.batchsearch);

		//Add the barcode list to the layout
		if (findViewById(R.id.batch_search_container) != null){
			if (savedInstanceState != null)
				return;

			BarcodeListFragment barcodeList = new BarcodeListFragment();
			getSupportFragmentManager().beginTransaction()
				.add(R.id.batch_search_container, barcodeList).commit();
		}
	}
}
