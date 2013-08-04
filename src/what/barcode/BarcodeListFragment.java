package what.barcode;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import what.gui.R;

/**
 * A fragment to display the list of barcodes saved on the device and
 * allow the user to select which one they want to search the site with
 */
public class BarcodeListFragment extends SherlockFragment {
	private static final String FILENAME = "barcodes.txt";
	private static File extStorageDirectory = Environment.getExternalStorageDirectory();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		LinearLayout v = (LinearLayout)inflater.inflate(R.layout.barcodelist, container, false);

		//Read in the list of barcodes and add them to the view
		try {
			Scanner scanner = new Scanner(new File(extStorageDirectory, FILENAME));
			scanner.useDelimiter(",");
			ArrayList<String> barcodes = new ArrayList<String>();
			while (scanner.hasNext())
				barcodes.add(scanner.next());

			//Print it all out for debugging & shove it in the view
			for (String s : barcodes){
				System.out.println(s);
				TextView tv = new TextView(getSherlockActivity());
				tv.setText(s);
				v.addView(tv);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return v;
	}

	/**
	 * Just write some junk to the barcode file for testing
	 */
	private void writeJunkUPC(){
		try {
			File file = new File(extStorageDirectory, FILENAME);
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);

			for (int i = 0; i < 10; ++i){
				StringBuilder sb = new StringBuilder();
				sb.append(i);
				sb.append(i * 5);
				sb.append(i * 10);
				sb.append(",");
				fileOutputStream.write(sb.toString().getBytes());
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
