package what.whatandroid.barcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class for managing the user's barcode database
 */
public class BarcodeDatabaseHelper extends SQLiteOpenHelper {
	public static final String TABLE = "barcodes", COL_ID = "_id", COL_UPC = "upc",
		COL_TERMS = "terms", COL_TAGS = "tags", COL_DATE = "date", COL_LABEL = "label";
	private static final String DB_NAME = "barcodes.db";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE = "CREATE TABLE " + TABLE + "("
		+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_UPC + " TEXT NOT NULL, "
		+ COL_DATE + " TEXT, " + COL_TERMS + " TEXT, " + COL_TAGS + " TEXT, " + COL_LABEL + " TEXT, "
		+ "UNIQUE(" + COL_UPC + "));";
	private static final String DB_BUILD_IDX = "CREATE INDEX upcidx ON " + TABLE + "(" + COL_UPC + ");";

	public BarcodeDatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		System.out.println("DB created");
		db.execSQL(DB_CREATE);
		db.execSQL(DB_BUILD_IDX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//Delete everything, if we do ever decide to change this database we'll have to preserve the data
		db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
		onCreate(db);
	}
}
