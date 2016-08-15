package de.theonlymarv.computermonitor.Database;

import android.provider.BaseColumns;

/**
 * Created by Marvin on 14.08.2016.
 */
public class DatabaseContract {
    public static final String DB_NAME = "cm.db";
    public static final int DB_VERSION = 1;

    public static abstract class LastConnectionEntry implements BaseColumns{
        public static final String TABLE_NAME = "LastConnection";
        public static final String COL_NAME = "connectionName";
        public static final String COL_URL = "connectionUrl";
        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (\n" +
                        "\t" + _ID + "\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "\t" + COL_NAME + "\tTEXT NOT NULL UNIQUE,\n" +
                        "\t" + COL_URL + "\tTEXT NOT NULL\n" +
                        ")";
    }
}
