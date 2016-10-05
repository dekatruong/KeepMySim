package com.dekatruong.keepmysim.dao;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Deka on 04/10/2016.
 */

public class SmsSendScheduleDao {

    public static final String PREFERENCES_FILE_KEY = "SmsSendScheduleDao";

    private Context context;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SmsSendScheduleDao(Context context) {
        //To do: validate context
        this.context = context;

        //
        this.sharedPref = context.getSharedPreferences(
                PREFERENCES_FILE_KEY,
                Context.MODE_PRIVATE);

        this.editor = sharedPref.edit();

    }

    public boolean save() {

        //Gson gson;

        return true;
    }

}
