package com.dekatruong.keepmysim.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dekatruong.keepmysim.dto.SmsSend;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;
import com.dekatruong.keepmysim.dao.MainDbHelper.SmsSendScheduleEntry;

import java.util.Calendar;
import java.util.GregorianCalendar;
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

    public long create(SmsSendSchedule mSmsSendSchedule) {

        // Gets the data repository in write mode
        SQLiteDatabase writableDbConnection = mDbHelper.getWritableDatabase(); //To do: connect once in init

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SmsSendScheduleEntry.COLUMN_REQUESTID, mSmsSendSchedule.getRequestCode());
        values.put(SmsSendScheduleEntry.COLUMN_SENDING_CALENDAR_MILLIS,
                mSmsSendSchedule.getSendingCalendar().getTimeInMillis()); //Note int and long
        values.put(SmsSendScheduleEntry.COLUMN_IS_REPEATING, mSmsSendSchedule.isRepeating());
        values.put(SmsSendScheduleEntry.COLUMN_INTERVAL, mSmsSendSchedule.getInterval());
        values.put(SmsSendScheduleEntry.COLUMN_IS_EXACT, mSmsSendSchedule.isExact());
        values.put(SmsSendScheduleEntry.COLUMN_SMSSEND_MESSAGE,
                mSmsSendSchedule.getSmsSend().getMessage());
        values.put(SmsSendScheduleEntry.COLUMN_SMSSEND_RECIPIENTS,
                mSmsSendSchedule.getSmsSend().getRecipientsString());
        values.put(SmsSendScheduleEntry.COLUMN_STATUS,
                mSmsSendSchedule.getStatus());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = writableDbConnection.insert(SmsSendScheduleEntry.TABLE_NAME, null, values);

        Log.i("MyApp-DAO", "create - new record id: "+newRowId);

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


    public List<SmsSendSchedule> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase(); //To do: connect once in init

        String sortOrder = SmsSendScheduleEntry._ID + " DESC";

        //Execute query
        Cursor cursor = db.query(
                SmsSendScheduleEntry.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //Fetch
        return fetchCursorToDtoList(cursor);
    }

    private static List<SmsSendSchedule> fetchCursorToDtoList(Cursor cursor) {
        cursor.moveToFirst();

        List<SmsSendSchedule> result_list = new LinkedList(); //To do: thinking list type
        while (cursor.isAfterLast() == false) {
            SmsSendSchedule dto = fetchCursorToDto(cursor);

            result_list.add(dto);

            cursor.moveToNext();
        }

        //Log.i("MyApp-DAO", "get all: "+result_list.toString());

        //Release connection
        cursor.close();

        return result_list;
    }

    private static SmsSendSchedule fetchCursorToDto(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(
                SmsSendScheduleEntry._ID));
        int request_id = cursor.getInt(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_REQUESTID));
        long sending_calendar_millis = cursor.getLong(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_SENDING_CALENDAR_MILLIS));
        boolean is_repeating = (1 == cursor.getInt(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_IS_REPEATING)));
        boolean is_exact = (1 == cursor.getInt(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_IS_EXACT)));
        long interval = cursor.getLong(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_INTERVAL));
        String smssend_message = cursor.getString(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_SMSSEND_MESSAGE));
        String smssend_recipients = cursor.getString(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_SMSSEND_RECIPIENTS));
        short status = cursor.getShort(cursor.getColumnIndex(
                SmsSendScheduleEntry.COLUMN_STATUS));

        /////
        Calendar sending_calendar = GregorianCalendar.getInstance(); sending_calendar.setTimeInMillis(sending_calendar_millis);

        SmsSend smssend = new SmsSend(SmsSend.fetchRecipients(smssend_recipients), smssend_message);

        SmsSendSchedule rs = new SmsSendSchedule(request_id, sending_calendar, smssend)
                .setRepeating(is_repeating)
                .setInterval(interval)
                .setExact(is_exact)
                .setStatus(status);
        //rs.setId(id); //to do: work with DB _id
        //To do: if Schedule has registered, fetch PendingIntent from request_id

        return rs;
    }

    public static SmsSendSchedule getById(int id) {
        return FAKE_DATA.get(id);
    }

    public static SmsSendSchedule getByRequetId(int request_id) {
        return FAKE_DATA.get(request_id);
    }
}
