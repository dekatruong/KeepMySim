package com.dekatruong.keepmysim.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Deka on 06/10/2016.
 */

public class MainDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Main";
    public static final String DATABASE_FILE_NAME = "Main.db";


    public MainDbHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    //@override
    public void onCreate(SQLiteDatabase db) {
        //Feed
        db.execSQL("CREATE TABLE " + SmsSendScheduleEntry.TABLE_NAME + " (" +
                SmsSendScheduleEntry._ID + " INTEGER PRIMARY KEY," +
                SmsSendScheduleEntry.COLUMN_REQUESTID + " BIGINT,"+
                SmsSendScheduleEntry.COLUMN_SENDING_CALENDAR_MILLIS + " BIGINT," +
                SmsSendScheduleEntry.COLUMN_IS_REPEATING + " INTEGER(1)," +
                SmsSendScheduleEntry.COLUMN_IS_EXACT + " INTEGER(1)," +
                SmsSendScheduleEntry.COLUMN_SMSSEND_MESSAGE + " TEXT," +
                SmsSendScheduleEntry.COLUMN_SMSSEND_RECIPIENTS + " TEXT," +
                ")"
        );
        //User
        //db.execSQL(SQL_CREATE_USER_ENTRIES);
    }

    //override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + SmsSendScheduleEntry.TABLE_NAME);
        onCreate(db);
    }

    //override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /* Inner class that defines the table contents */
    public static class SmsSendScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "sms_send_schedule";

        public static final String COLUMN_REQUESTID = "request_id";
        public static final String COLUMN_SENDING_CALENDAR_MILLIS = "sending_calendar_millis";
        public static final String COLUMN_IS_REPEATING = "is_repeating";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_IS_EXACT = "is_exact";
        public static final String COLUMN_SMSSEND_MESSAGE = "smssend_message";
        public static final String COLUMN_SMSSEND_RECIPIENTS = "smssend_recipients";
    }

    /* Inner class that defines the table contents */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
    }
}
