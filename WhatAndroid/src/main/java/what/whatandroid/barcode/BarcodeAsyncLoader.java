package what.whatandroid.barcode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.AsyncTaskLoader;
import api.barcode.Barcode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * We don't use the Barcodes as a content provider so we don't use the CursorLoader
 */
public class BarcodeAsyncLoader extends AsyncTaskLoader<List<Barcode>> {
	private BarcodeDatabaseHelper dbHelper;
	private List<Barcode> barcodes;
	private final String[] allCols = {BarcodeDatabaseHelper.COL_UPC, BarcodeDatabaseHelper.COL_DATE,
		BarcodeDatabaseHelper.COL_TERMS, BarcodeDatabaseHelper.COL_TAGS, BarcodeDatabaseHelper.COL_LABEL};

	public BarcodeAsyncLoader(Context context){
		super(context);
		dbHelper = new BarcodeDatabaseHelper(context);
	}

	@Override
	public List<Barcode> loadInBackground(){
		barcodes = null;
		try {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			if (database != null){
				Cursor cursor = database.query(BarcodeDatabaseHelper.TABLE, allCols, null, null, null, null,
					BarcodeDatabaseHelper.COL_DATE + " DESC");
				cursor.moveToFirst();
				barcodes = new ArrayList<Barcode>();
				while (!cursor.isAfterLast()){
					barcodes.add(readBarcode(cursor));
					cursor.moveToNext();
				}
				cursor.close();
				database.close();
			}
		}
		catch (SQLiteException e){
			e.printStackTrace();
		}
		return barcodes;
	}

	private Barcode readBarcode(Cursor cursor){
		return new Barcode(cursor.getString(0), new Date(cursor.getLong(1)),
			cursor.getString(2), cursor.getString(3), cursor.getString(4));
	}

	@Override
	protected void onStartLoading(){
		if (barcodes != null){
			deliverResult(barcodes);
		}
		if (takeContentChanged() || barcodes == null){
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading(){
		cancelLoad();
	}
}
