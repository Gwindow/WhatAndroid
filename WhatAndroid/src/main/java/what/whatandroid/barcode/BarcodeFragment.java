package what.whatandroid.barcode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.barcode.Barcode;
import api.son.MySon;
import com.google.gson.reflect.TypeToken;
import what.whatandroid.R;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * The barcode scanner fragment, user can view the list of saved barcodes,
 * edit them and be redirected to ZXing Barcode scanner to scan more
 */
public class BarcodeFragment extends Fragment {
	private BarcodeAdapter barcodeAdapter;
	private TextView noBarcodes;
	/**
	 * Barcode list type token for GSON
	 */
	private static final Type LIST_TYPE = new TypeToken<List<Barcode>>() {
	}.getType();

	public BarcodeFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		noBarcodes = (TextView)view.findViewById(R.id.no_content_notice);
		noBarcodes.setText("No barcodes");
		barcodeAdapter = new BarcodeAdapter(getActivity());
		list.setAdapter(barcodeAdapter);

		new LoadBarcodes().execute();
		return view;
	}

	@Override
	public void onPause(){
		super.onPause();
		new SaveBarcodes().execute(barcodeAdapter.getBarcodes());
	}

	/**
	 * Async task to load the user's barcodes from the file
	 */
	private class LoadBarcodes extends AsyncTask<Void, Void, List<Barcode>> {
		@Override
		protected List<Barcode> doInBackground(Void... params){
			if (isAdded()){
				try {
					BufferedInputStream in = new BufferedInputStream(getActivity().openFileInput("barcodes.json"));
					return (List<Barcode>)MySon.toObject(in, LIST_TYPE);
				}
				catch (FileNotFoundException e){
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute(){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(true);
				getActivity().setProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		protected void onPostExecute(List<Barcode> barcodes){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(false);
				getActivity().setProgressBarIndeterminateVisibility(false);
			}
			if (barcodes == null || barcodes.isEmpty()){
				noBarcodes.setVisibility(View.VISIBLE);
			}
			else {
				barcodeAdapter.addAll(barcodes);
				barcodeAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Async task to save the user's barcodes to the file. param[0] should be the barcodes to save
	 */
	private class SaveBarcodes extends AsyncTask<List<Barcode>, Void, Boolean> {
		@Override
		protected Boolean doInBackground(List<Barcode>... barcodes){
			if (isAdded()){
				try {
					FileOutputStream out = getActivity().openFileOutput("barcodes.json", Context.MODE_PRIVATE);
					MySon.toFile(barcodes[0], new FileWriter(out.getFD()), LIST_TYPE);
					return true;
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPreExecute(){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(true);
				getActivity().setProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (isAdded()){
				getActivity().setProgressBarIndeterminate(false);
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (status){
					Toast.makeText(getActivity(), "Barcodes saved", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getActivity(), "Could not save barcodes", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
