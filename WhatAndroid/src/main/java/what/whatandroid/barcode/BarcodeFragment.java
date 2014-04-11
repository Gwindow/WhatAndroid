package what.whatandroid.barcode;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.barcode.Barcode;
import api.soup.MySoup;
import what.whatandroid.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The barcode scanner fragment, user can view the list of saved barcodes,
 * edit them and be redirected to ZXing Barcode scanner to scan more
 */
public class BarcodeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Barcode>> {
	private BarcodeAdapter barcodeAdapter;
	private TextView noBarcodes;

	public BarcodeFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		//See if we got an intent containing some barcodes to add
		Intent intent = activity.getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/csv")){
			Uri history = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			ContentResolver resolver = activity.getContentResolver();
			if (history != null){
				try {
					List<Barcode> barcodes = new ArrayList<Barcode>();
					InputStream in = new BufferedInputStream(resolver.openInputStream(history));
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					for (String line = reader.readLine(); line != null; line = reader.readLine()){
						String info[] = line.replaceAll("\"", "").split(",");
						//Read the barcode info for exported product barcodes only
						if (info[2].matches("(UPC|EAN|RSS)_.+")){
							Date scanned = new Date(Long.parseLong(info[3]));
							barcodes.add(new Barcode(info[0], scanned));
						}
					}
					new SaveBarcodesTask(false).execute(barcodes);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		noBarcodes = (TextView)view.findViewById(R.id.no_content_notice);
		noBarcodes.setText("No barcodes");
		barcodeAdapter = new BarcodeAdapter(getActivity());
		list.setAdapter(barcodeAdapter);
		getLoaderManager().initLoader(0, null, this);
		return view;
	}

	@Override
	public void onStop(){
		super.onStop();
		new SaveBarcodesTask(true).execute(barcodeAdapter.getBarcodes());
	}

	@Override
	public Loader<List<Barcode>> onCreateLoader(int id, Bundle args){
		return new BarcodeAsyncLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Barcode>> loader, List<Barcode> data){
		if (data == null || data.isEmpty()){
			noBarcodes.setVisibility(View.VISIBLE);
		}
		else {
			barcodeAdapter.clear();
			barcodeAdapter.addAll(data);
			barcodeAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Barcode>> loader){
	}

	/**
	 * Async task to write some barcodes to the database. Params[0] should be the list of barcodes to add
	 */
	private class SaveBarcodesTask extends AsyncTask<List<Barcode>, Void, Boolean> {
		private boolean replace;
		private BarcodeDatabaseHelper helper;

		/**
		 * Context to open database in. Replace indicates that we should replace
		 * conflicting UPC entries with the new one passed, if false then the conflicting
		 * UPC won't be saved
		 */
		public SaveBarcodesTask(boolean replace){
			this.replace = replace;
			helper = new BarcodeDatabaseHelper(getActivity());
		}

		@Override
		protected Boolean doInBackground(List<Barcode>... params){
			try {
				SQLiteDatabase database = helper.getWritableDatabase();
				if (database != null){
					String cmd = "INSERT OR " + (replace ? "REPLACE" : "IGNORE") + " INTO ";
					SQLiteStatement statement = database.compileStatement(cmd
						+ BarcodeDatabaseHelper.TABLE + "(" + BarcodeDatabaseHelper.COL_UPC + ", " + BarcodeDatabaseHelper.COL_DATE
						+ ", " + BarcodeDatabaseHelper.COL_TERMS + ", " + BarcodeDatabaseHelper.COL_TAGS
						+ ", " + BarcodeDatabaseHelper.COL_LABEL + ") VALUES(?, ?, ?, ?, ?);");
					for (Barcode b : params[0]){
						statement.bindString(1, b.getUpc());
						statement.bindString(2, MySoup.writeDate(b.getAdded()));
						statement.bindString(3, b.getSearchTerms());
						statement.bindString(4, b.getSearchTags());
						statement.bindString(5, b.getUserLabel());
						statement.executeInsert();
					}
				}
				return true;
			}
			catch (SQLiteException e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status){
			if (isAdded()){
				if (status){
					Toast.makeText(getActivity(), "Barcodes saved", Toast.LENGTH_SHORT).show();
					//Reload viewed barcodes
					getLoaderManager().restartLoader(0, null, BarcodeFragment.this);
				}
				else {
					Toast.makeText(getActivity(), "Could not save barcodes", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
