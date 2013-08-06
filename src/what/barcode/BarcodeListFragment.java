package what.barcode;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import api.barcode.Barcode;
import api.son.MySon;
import what.gui.R;

/**
 * A fragment to display the list of barcodes saved on the device and
 * allow the user to select which one they want to search the site with
 */
public class BarcodeListFragment extends SherlockListFragment {
	private static final String FILENAME = "barcodes.json";
	private static File extStorageDirectory = Environment.getExternalStorageDirectory();
	Type barcodeListType = new TypeToken<List<Barcode>>(){}.getType();
	private BarcodeAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		File file = new File(extStorageDirectory, FILENAME);
		List<Barcode> barcodes = (List<Barcode>) MySon.toObjectFromFile(file, barcodeListType);
		//If the file was empty/not found just put an empty list
		if (barcodes == null)
			barcodes = new ArrayList<Barcode>();

		adapter = new BarcodeAdapter(getSherlockActivity(), R.layout.barcode_info, barcodes);
		setListAdapter(adapter);
	}

	/**
	 * When the fragment is going to be killed save any changes made to the barcode file
 	 */
	@Override
	public void onStop(){
		super.onStop();
		try {
			File file = new File(extStorageDirectory, FILENAME);
			FileWriter writer = new FileWriter(file);

			List<Barcode> barcodes = adapter.getItems();
			writer.write(MySon.toJson(barcodes, barcodeListType));
			writer.flush();
			writer.close();
		}
		catch (IOException e){
			//TODO Put up a toast saying we failed saving barcode changes?
			e.printStackTrace();
		}
	}
}
