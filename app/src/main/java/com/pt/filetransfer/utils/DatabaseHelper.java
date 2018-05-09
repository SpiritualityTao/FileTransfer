package com.pt.filetransfer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 韬 on 2017-05-13.
 * 数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    
    public static final String HISTORY_INFO = "History_Info";

    private static final String KEY_ID = "_id";

    private static final String HISTORY_CREATE = "create table " +  HISTORY_INFO +
            " (" + KEY_ID + " integer primary key autoincrement, " +
            "fName text not null," +
            "fPath text not null," +
            "fType integer," +
            "actionType integer," +
            "senderName text not null," +
            "time integer)";
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_INFO);
        onCreate(db);
    }
}
