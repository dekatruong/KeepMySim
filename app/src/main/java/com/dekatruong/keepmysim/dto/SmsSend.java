package com.dekatruong.keepmysim.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by Deka on 26/09/2016.
 */

public class SmsSend implements Parcelable {
    public static final String EXTRA_KEY_PHONE = "phone";
    public static final String EXTRA_KEY_MESSAGE = "message";

    private static Gson gson = new Gson();



    //private String recipient;
    private List<String> recipients = new LinkedList();
    private String message;

    public SmsSend(List<String> recipients, String message){
        this.message = message;
        this.getRecipients().addAll(recipients);
    }

    public SmsSend(String message, String... recipients){
        this.message = message;
        this.getRecipients().addAll(Arrays.asList(recipients));
    }

    //////////////////////////////////////////////////////
    //Parcelable
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SmsSend createFromParcel(Parcel in) {
            return new SmsSend(in);
        }

        public SmsSend[] newArray(int size) {
            return new SmsSend[size];
        }
    };

    public SmsSend(Parcel in) {
        this.message = in.readString();
        in.readStringList(this.recipients);
        //this.recipients = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeStringList(this.recipients);
    }
    ////////////////////////////////////////////////////////

    public String getRecipientsString() {
        String result = "";

        for (String recipient: getRecipients()) {
            result += recipient + ",";
        }

        result = result.replaceFirst(",+$", "");

        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> addRecipient(String recipient) {
        this.getRecipients().add(recipient);

        return this.getRecipients();
    }

    public SmsSend setRecipients(List<String> recipients) {
        this.recipients = recipients;

        return this;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getFirstRecipient() {
        if(!recipients.isEmpty()) {
            return recipients.iterator().next();
        } else {
            //To do: should throw Exception
            return "";
        }
    }

    public String toJson() {
        return SmsSend.gson.toJson(this);
    }

    public static SmsSend fromJson(String json_string) {
        return SmsSend.gson.fromJson(json_string, SmsSend.class);
    }

}
