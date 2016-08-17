package de.theonlymarv.computermonitor.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Marvin on 14.08.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context){
        super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
        Log.i(TAG, "DatabaseHelper created a new database: " + getDatabaseName());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating table : " + DatabaseContract.LastConnectionEntry.SQL_CREATE);
            db.execSQL(DatabaseContract.LastConnectionEntry.SQL_CREATE);
        }
        catch (Exception ex){
            Log.e(TAG, "Error creating table: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading Datebase");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.LastConnectionEntry.TABLE_NAME);
        onCreate(db);
    }
}
