package de.theonlymarv.computermonitor.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Marvin on 14.08.2016.
 */
public class DatabaseSource {
    private static DatabaseSource instance;

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private DatabaseSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public static DatabaseSource getInstance(Context context){
        if (instance == null){
            instance = new DatabaseSource(context);
        }
        return instance;
    }

    public SQLiteDatabase open(){
        database = dbHelper.getWritableDatabase();
        return database;
    }

    public void close(){
        dbHelper.close();
    }
}
