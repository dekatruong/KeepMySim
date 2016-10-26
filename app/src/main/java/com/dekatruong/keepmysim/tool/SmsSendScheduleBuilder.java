package com.dekatruong.keepmysim.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.dekatruong.keepmysim.dto.SmsSend;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * user Pref storage keep next request-code of Intent
 * <p>
 * Created by Deka on 10/10/2016.
 */
public class SmsSendScheduleBuilder {

    private static final String SMSSEND_SHEDULE_PREF = "smsend_schedule_pref";
    private static final String NEXT_REQUEST_CODE_KEY = "next_request_code";

    private SmsSendSchedule mSmsSendSchedule;
    private AtomicInteger nextRequestCode = new AtomicInteger(1); //To do: care with asyn task

    SharedPreferences sharedPref;
    SharedPreferences.Editor mSharedPrefEditor;


    public SmsSendScheduleBuilder(Context context) {
        this.sharedPref = context.getSharedPreferences( SMSSEND_SHEDULE_PREF, Context.MODE_PRIVATE);
        this.mSharedPrefEditor = sharedPref.edit();

        nextRequestCode.set(sharedPref.getInt(NEXT_REQUEST_CODE_KEY, 1)); //Load From storage

        mSmsSendSchedule = new SmsSendSchedule();
    }


    public SmsSendScheduleBuilder(Context context, Calendar sending_calendar, SmsSend sms_send) {
        this.sharedPref = context.getSharedPreferences( SMSSEND_SHEDULE_PREF, Context.MODE_PRIVATE);
        this.mSharedPrefEditor = sharedPref.edit();

        nextRequestCode.set(sharedPref.getInt(NEXT_REQUEST_CODE_KEY, 1)); //Load From storage

        mSmsSendSchedule = new SmsSendSchedule(0, sending_calendar, sms_send); //dont set nextRequestCode
    }


    public SmsSendScheduleBuilder generateRequestCode() {
        int next_request_code = this.getAndIncrementAndStorgeNextRequestCode();

        mSmsSendSchedule.setRequestCode(next_request_code); //To dto

        return this;
    }

    public SmsSendScheduleBuilder generateRequestCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SMSSEND_SHEDULE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //get an increase
        int next_request_code = this.nextRequestCode.get(); //To do: care with asyn task
        editor.putInt(NEXT_REQUEST_CODE_KEY, this.nextRequestCode.incrementAndGet());
        editor.commit();
        //
        mSmsSendSchedule.setRequestCode(next_request_code); //To dto

        return this;
    }

    public SmsSendSchedule build() {
        return mSmsSendSchedule;
    }

    private int getAndIncrementAndStorgeNextRequestCode() {
        int old_next_request_code = this.nextRequestCode.getAndIncrement(); //To do: care with asyn task
        mSharedPrefEditor.putInt(NEXT_REQUEST_CODE_KEY, this.nextRequestCode.get());
        mSharedPrefEditor.commit();

        return old_next_request_code;
    }

    public int getNextRequestCode() {
        return nextRequestCode.get();
    }

    public void setNextRequestCode(int nextRequestCode) {
        this.nextRequestCode.set(nextRequestCode);
    }
}
