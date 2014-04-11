package what.whatandroid.barcode;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import api.barcode.Barcode;

/**
 * Task to update an existing barcode entry with new data
 */
public class UpdateBarcodeTask extends AsyncTask<Barcode, Void, Boolean> {
	private BarcodeDatabaseHelper helper;

	public UpdateBarcodeTask(Context context){
		helper = new BarcodeDatabaseHelper(context);
	}

	@Override
	protected Boolean doInBackground(Barcode... params){
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			if (database != null){
				SQLiteStatement statement = database.compileStatement("UPDATE " + BarcodeDatabaseHelper.TABLE
					+ " SET " + BarcodeDatabaseHelper.COL_DATE + "=?, " + BarcodeDatabaseHelper.COL_TERMS + "=?, "
					+ BarcodeDatabaseHelper.COL_TAGS + "=?, " + BarcodeDatabaseHelper.COL_LABEL + "=? "
					+ "WHERE " + BarcodeDatabaseHelper.COL_UPC + "=?");
				statement.bindLong(1, params[0].getAdded().getTime());
				statement.bindString(2, params[0].getSearchTerms());
				statement.bindString(3, params[0].getSearchTags());
				statement.bindString(4, params[0].getUserLabel());
				statement.bindString(5, params[0].getUpc());
				statement.executeUpdateDelete();
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
}