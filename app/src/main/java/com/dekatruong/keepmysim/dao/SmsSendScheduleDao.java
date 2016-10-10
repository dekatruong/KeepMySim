package com.dekatruong.keepmysim.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.dekatruong.keepmysim.dto.SmsSend;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;
import com.dekatruong.keepmysim.dao.MainDbHelper.SmsSendScheduleEntry;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Deka on 04/10/2016.
 */

public class SmsSendScheduleDao extends MainDbBaseDao {

    public SmsSendScheduleDao(Context context) {
        super(context);
    }

    public long save(SmsSendSchedule mSmsSendSchedule) {

        // Gets the data repository in write mode
        SQLiteDatabase writableDbConnection = mDbHelper.getWritableDatabase(); //To do: connect once in init

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SmsSendScheduleEntry.COLUMN_REQUESTID, mSmsSendSchedule.getRequestId());
        values.put(SmsSendScheduleEntry.COLUMN_SENDING_CALENDAR_MILLIS,
                mSmsSendSchedule.getSendingCalendar().getTimeInMillis()); //Note int and long
        values.put(SmsSendScheduleEntry.COLUMN_IS_REPEATING, mSmsSendSchedule.isRepeating());
        values.put(SmsSendScheduleEntry.COLUMN_IS_EXACT, mSmsSendSchedule.isExact());
        values.put(SmsSendScheduleEntry.COLUMN_SMSSEND_MESSAGE, mSmsSendSchedule.getSmsSend().getMessage());
        values.put(SmsSendScheduleEntry.COLUMN_SMSSEND_RECIPIENTS, mSmsSendSchedule.getSmsSend().getRecipientsString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = writableDbConnection.insert(SmsSendScheduleEntry.TABLE_NAME, null, values);

        //Release connection
        writableDbConnection.close();

        return newRowId;
    }

    //Currently, we use fake data. To do: storge them
    public static Map<Integer, SmsSendSchedule> FAKE_DATA = new LinkedHashMap();

    static {
        FAKE_DATA.put(1,
                new SmsSendSchedule(1,
                        Calendar.getInstance(),
                        new SmsSend("message test 1", "0986432189")));
        FAKE_DATA.put(2,
                new SmsSendSchedule(2,
                        Calendar.getInstance(),
                        new SmsSend("message test 2", "01646424198")));
        FAKE_DATA.put(3,
                new SmsSendSchedule(3,
                        Calendar.getInstance(),
                        new SmsSend("message test 3", "0986432189", "01646424198")));
    }
//
//    public Map<Integer, SmsSendSchedule> getAll() {
//        return FAKE_DATA;
//    }


    public static List<SmsSendSchedule> getAll() {
        return new LinkedList(FAKE_DATA.values());
    }

    public static SmsSendSchedule getById(int id) {
        return FAKE_DATA.get(id);
    }
}
