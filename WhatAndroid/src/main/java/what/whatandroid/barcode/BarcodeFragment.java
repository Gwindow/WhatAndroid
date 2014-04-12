package what.whatandroid.barcode;

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
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.barcode.Barcode;
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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		//See if we got an intent containing some barcodes to add, we also don't want to re-handle an
		//intent if orientation changed or such, so ignore it if we've got a saved state
		if (savedInstanceState == null){
			Intent intent = getActivity().getIntent();
			String action = intent.getAction();
			String type = intent.getType();
			if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/csv")){
				Uri history = intent.getParcelableExtra(Intent.EXTRA_STREAM);
				ContentResolver resolver = getActivity().getContentResolver();
				if (history != null){
					try {
						List<Barcode> barcodes = new ArrayList<Barcode>();
						InputStream in = new BufferedInputStream(resolver.openInputStream(history));
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						for (String line = reader.readLine(); line != null; line = reader.readLine()){
							String info[] = line.replaceAll("\"", "").split(",");
							//Read the barcode info for exported product barcodes only
							if (info[2].matches("(UPC|EAN|RSS)_.+")){
								barcodes.add(new Barcode(info[0], new Date(Long.parseLong(info[3]))));
							}
						}
						new CreateBarcodesTask().execute(barcodes);
					}
					catch (Exception e){
						e.printStackTrace();
					}
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
		barcodeAdapter = new BarcodeAdapter(getActivity(), noBarcodes);
		list.setAdapter(barcodeAdapter);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		//We always want to read the latest information from the database
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.barcodes, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_delete){
			new DeleteBarcodeTask(getActivity()).execute();
			barcodeAdapter.clear();
			barcodeAdapter.notifyDataSetChanged();
			noBarcodes.setVisibility(View.VISIBLE);
			return true;
		}
		else if (item.getItemId() == R.id.action_new){
			new ScannerDialog().show(getChildFragmentManager(), "scanner_dialog");
			return true;
		}
		return false;
	}

	@Override
	public Loader<List<Barcode>> onCreateLoader(int id, Bundle args){
		return new BarcodeAsyncLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Barcode>> loader, List<Barcode> data){
		barcodeAdapter.clear();
		if (data == null || data.isEmpty()){
			noBarcodes.setVisibility(View.VISIBLE);
		}
		else {
			noBarcodes.setVisibility(View.GONE);
			barcodeAdapter.addAll(data);
		}
		barcodeAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<Barcode>> loader){
	}

	/**
	 * Add some barcode to the database and the viewed list of barcodes
	 */
	public void addBarcode(Barcode b){
		List<Barcode> barcodes = new ArrayList<Barcode>();
		barcodes.add(b);
		new CreateBarcodesTask().execute(barcodes);
	}

	/**
	 * Async task to write new barcodes to the database, if an entry already exists with the
	 * same UPC the barcode is ignored.
	 */
	private class CreateBarcodesTask extends AsyncTask<List<Barcode>, Void, Boolean> {
		private BarcodeDatabaseHelper helper;

		public CreateBarcodesTask(){
			helper = new BarcodeDatabaseHelper(getActivity());
		}

		@Override
		protected Boolean doInBackground(List<Barcode>... params){
			try {
				SQLiteDatabase database = helper.getWritableDatabase();
				if (database != null){
					SQLiteStatement statement = database.compileStatement("INSERT OR IGNORE INTO "
						+ BarcodeDatabaseHelper.TABLE + "(" + BarcodeDatabaseHelper.COL_UPC + ", "
						+ BarcodeDatabaseHelper.COL_DATE + ", " + BarcodeDatabaseHelper.COL_TERMS
						+ ", " + BarcodeDatabaseHelper.COL_TAGS + ", " + BarcodeDatabaseHelper.COL_LABEL
						+ ") VALUES(?, ?, ?, ?, ?);");
					for (Barcode b : params[0]){
						statement.bindString(1, b.getUpc());
						statement.bindLong(2, b.getAdded().getTime());
						statement.bindString(3, b.getSearchTerms());
						statement.bindString(4, b.getSearchTags());
						statement.bindString(5, b.getUserLabel());
						statement.executeInsert();
					}
					statement.close();
					database.close();
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
			if (status){
				Toast.makeText(getActivity(), "Barcodes added", Toast.LENGTH_SHORT).show();
				//Reload viewed barcodes
				getLoaderManager().restartLoader(0, null, BarcodeFragment.this);
			}
			else {
				Toast.makeText(getActivity(), "Could not add barcodes", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
