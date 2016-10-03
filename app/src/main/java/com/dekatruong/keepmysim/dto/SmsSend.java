package com.dekatruong.keepmysim.dto;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Deka on 26/09/2016.
 */

public class SmsSend {
    public static final String EXTRA_KEY_PHONE = "phone";
    public static final String EXTRA_KEY_MESSAGE = "message";

    //private String recipient;
    private Set<String> recipients = new LinkedHashSet();
    private String message;

    public SmsSend(Set<String> recipients, String message){
        this.message = message;
        this.recipients.addAll(recipients);
    }

    public String getRecipientsString() {
        String result = "";

        for (String recipient: recipients) {
            result += recipient + ",";
        }

        result.replaceAll(",+$", "");;

        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> addRecipient(String recipient) {
        this.recipients.add(recipient);

        return  this.recipients;
    }
}
