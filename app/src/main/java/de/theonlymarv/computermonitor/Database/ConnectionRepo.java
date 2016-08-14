package de.theonlymarv.computermonitor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.Models.Connection;

/**
 * Created by Marvin on 14.08.2016.
 */
public class ConnectionRepo {
    private DatabaseSource databaseSource;
    private String[] columns = {
            DatabaseContract.LastConnectionEntry.COL_NAME,
            DatabaseContract.LastConnectionEntry.COL_URL
    };

    public ConnectionRepo(Context context){
        databaseSource = DatabaseSource.getInstance(context);
    }

    private ContentValues getContentValues(Connection connection){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.LastConnectionEntry.COL_NAME, connection.getName());
        values.put(DatabaseContract.LastConnectionEntry.COL_URL, connection.getUrl());
        return values;
    }

    private Connection cursorToConnection(Cursor cursor){
        return new Connection(
                cursor.getString(cursor.getColumnIndex(DatabaseContract.LastConnectionEntry.COL_NAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.LastConnectionEntry.COL_URL))
        );
    }

    public int insertConnection(Connection connection){
        SQLiteDatabase db = databaseSource.open();
        ContentValues values = getContentValues(connection);

        long connectionId = db.insert(DatabaseContract.LastConnectionEntry.TABLE_NAME, null, values);
        databaseSource.close();
        return (int)connectionId;
    }

    public void deleteConnection(Connection connection){
        SQLiteDatabase db = databaseSource.open();
        String where = DatabaseContract.LastConnectionEntry.COL_URL + "=" + connection.getUrl();
        db.delete(DatabaseContract.LastConnectionEntry.TABLE_NAME, where, null);
        databaseSource.close();
    }

    public void deleteAllConnections() {
        SQLiteDatabase db = databaseSource.open();
        db.delete(DatabaseContract.LastConnectionEntry.TABLE_NAME, null, null);
        databaseSource.close();
    }

    public void updateStudent(Connection connection) {
        SQLiteDatabase db = databaseSource.open();
        ContentValues values = getContentValues(connection);
        String where = DatabaseContract.LastConnectionEntry.COL_NAME + "=" + connection.getName();
        db.update(DatabaseContract.LastConnectionEntry.TABLE_NAME, values, where, null);
        databaseSource.close();
    }

    @NonNull
    public List<Connection> getAllConnections() {
        SQLiteDatabase db = databaseSource.open();

        Cursor cursor = db.query(DatabaseContract.LastConnectionEntry.TABLE_NAME, columns, null, null, null, null, null);
        ArrayList<Connection> connections = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                connections.add(cursorToConnection(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        databaseSource.close();
        return connections;
    }
}
