package what.whatandroid.barcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.Toast;
import api.barcode.Barcode;

/**
 * Async task to delete some barcode from the database no parameters will indicate
 * to clear all barcodes
 */
public class DeleteBarcodeTask extends AsyncTask<Barcode, Void, Boolean> {
	private final Context context;
	private BarcodeDatabaseHelper helper;

	public DeleteBarcodeTask(Context c){
		context = c;
		helper = new BarcodeDatabaseHelper(context);
	}

	@Override
	protected Boolean doInBackground(Barcode... params){
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			if (database != null){
				boolean status;
				if (params.length == 0){
					database.delete(BarcodeDatabaseHelper.TABLE, null, null);
					status = true;
				}
				else {
					status = database.delete(BarcodeDatabaseHelper.TABLE,
						BarcodeDatabaseHelper.COL_UPC + "='" + params[0].getUpc() + "';", null) > 0;
				}
				database.close();
				return status;
			}
		}
		catch (SQLiteException e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean status){
		if (!status){
			Toast.makeText(context, "Could not delete barcode", Toast.LENGTH_SHORT).show();
		}
	}
}
