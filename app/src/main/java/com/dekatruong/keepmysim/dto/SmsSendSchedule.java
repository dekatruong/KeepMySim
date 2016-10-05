package com.dekatruong.keepmysim.dto;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * Created by Deka on 03/10/2016.
 */

public class SmsSendSchedule implements Parcelable {

    private static final int MILIS_PER_MINUTE = 60 * 1000;
    private static final int MILIS_PER_HOUR = MILIS_PER_MINUTE * 60;
    private static final int MILIS_PER_DAY  = MILIS_PER_HOUR * 24;

    public static final String EXTRA_KEY_SMSSEND = "smsSend";

    private static Gson gson = new Gson();

    ///////////////////////////////////////////////////////////////////
    private int id; //as request code, Unique each Schedule Alarm

    private Calendar sendingCalendar; //first time if repeating
    //for repeating
    private boolean isRepeating = false;
    private long interval;
    private boolean isExact = false;

    private SmsSend smsSend;

    private PendingIntent requestPendingIntent; //handle(remove/change) request after request be sent

    //////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////
    //Parcelable
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SmsSendSchedule createFromParcel(Parcel in) {
            return new SmsSendSchedule(in);
        }

        public SmsSendSchedule[] newArray(int size) {
            return new SmsSendSchedule[size];
        }
    };

    public SmsSendSchedule(Parcel in) {
        this.id = in.readInt();
        long milis = in.readLong(); //Note: read before because of order
        this.sendingCalendar = new GregorianCalendar(TimeZone.getTimeZone(in.readString()));
        this.sendingCalendar.setTimeInMillis(milis);
        this.isRepeating = (in.readByte()==1);
        this.interval = in.readLong();
        this.isExact = (in.readByte()==1);
        this.smsSend = in.readParcelable(SmsSend.class.getClassLoader());
        this.requestPendingIntent = in.readParcelable(PendingIntent.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.sendingCalendar.getTimeInMillis());
        dest.writeString(this.sendingCalendar.getTimeZone().getID());
        dest.writeByte((byte)(this.isRepeating?0b1:0b0));
        dest.writeLong(this.interval);
        dest.writeByte((byte)(this.isExact?0b1:0b0));
        dest.writeParcelable(this.smsSend, flags);
        dest.writeParcelable(this.requestPendingIntent, flags);
    }
    ////////////////////////////////////////////////////////

    public SmsSendSchedule() {

    }

    public SmsSend getSmsSend() {
        return smsSend;
    }

    public SmsSendSchedule setSmsSend(SmsSend smsSend) {
        this.smsSend = smsSend;

        return this;
    }

    public int getId() {
        return id;
    }

    public SmsSendSchedule setId(int id) {
        this.id = id;

        return this;
    }

    public Calendar getSendingCalendar() {
        return sendingCalendar;
    }

    public SmsSendSchedule setSendingCalendar(Calendar sendingCalendar) {
        this.sendingCalendar = sendingCalendar;

        return this;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public SmsSendSchedule setRepeating(boolean repeating) {
        isRepeating = repeating;

        return this;
    }

    public long getInterval() {
        return interval;
    }

    public SmsSendSchedule setInterval(long interval) {
        this.interval = interval;

        return this;
    }

    public boolean isExact() {
        return isExact;
    }

    public void setExact(boolean exact) {
        isExact = exact;
    }

    public PendingIntent getRequestPendingIntent() {
        return requestPendingIntent;
    }

    public void setRequestPendingIntent(PendingIntent requestPendingIntent) {
        this.requestPendingIntent = requestPendingIntent;
    }

    public String toJson() {
        return SmsSendSchedule.gson.toJson(this);
    }

    public static SmsSendSchedule fromJson(String json_string) {
        return SmsSendSchedule.gson.fromJson(json_string, SmsSendSchedule.class);
    }

}
