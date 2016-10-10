package com.dekatruong.keepmysim.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Deka on 09/10/2016.
 */

abstract public class MainDbBaseDao {

    protected MainDbHelper mDbHelper;

    protected SQLiteDatabase readableDbConnection;
    protected SQLiteDatabase writableDbConnection;

    public MainDbBaseDao(Context context) {
        this.mDbHelper = new MainDbHelper(context);

        //To do: may long-run task
        //this.connectDB();
    }

    public void connectDB() {
        readableDbConnection = mDbHelper.getReadableDatabase();
        writableDbConnection = mDbHelper.getWritableDatabase();
    }

    public void connectReadableDB() {
        readableDbConnection = mDbHelper.getReadableDatabase();
    }

    public void connectWritableDB() {
        readableDbConnection = mDbHelper.getReadableDatabase();
    }
}
