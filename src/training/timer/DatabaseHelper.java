package training.timer;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
	
	private SQLiteDatabase database;
	
	private static final String DB_NAME = "trainingTimerDB";
	private static final String CREATE_TBL_TRAININGS = "CREATE TABLE IF NOT EXISTS trainings ("+ BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, description VARCHAR)";
	private static final String CREATE_TBL_WORKOUTS = "CREATE TABLE IF NOT EXISTS workouts ("+ BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, training_id INTEGER, order_number INTEGER, name VARCHAR, description VARCHAR,  type VARCHAR,  duration INTEGER)";

	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	
	//Opens db and sets it as member variable that getInstance will take
	public SQLiteDatabase openAndReturnDb() throws SQLException {
		
		database = this.getWritableDatabase();
		
		Log.d("DatabaseHelper", "open()");
		return database;
	}
	
	public void close() throws SQLException {
		database.close();
		Log.d("DatabaseHelper", "close()");
	}

	/*
	 * Create all necessary tables in the database
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TBL_TRAININGS);
		db.execSQL(CREATE_TBL_WORKOUTS);
		
		Log.d("onCreate", "We are in onCreate !!!");
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Steps to upgrade the database for the new version ...
	}


	
	
}