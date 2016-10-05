package com.dekatruong.keepmysim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.dekatruong.keepmysim.dto.SmsSend;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;

public class SmsSendingAlarmReceiver extends BroadcastReceiver {

    private SmsManager smsManager = SmsManager.getDefault();

    public SmsSendingAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyApp", "onReceive - SmsSendingAlarmReceiver");

        //Extras
        SmsSend aSmsSend = intent.getExtras().getParcelable(SmsSendSchedule.EXTRA_KEY_SMSSEND);

        this.sendSms(context, aSmsSend);
    }


    private void sendSms(Context context, String phone, String message) {
        String log = "Sending Message:\n" +
                " phone: " + phone + "\n" +
                " message: " + message;
        Log.i("MyApp", log);

        // To do: permission problem
        this.smsManager.sendTextMessage(
                phone,
                null,
                message,
                null, null);

        Toast.makeText(context, log,
                Toast.LENGTH_SHORT).show();

    }

    private void sendSms(Context context, SmsSend aSmsSend) {
        String log = "Sending Message:\n" +
                " phone: " + aSmsSend.getRecipientsString() + "\n" +
                " message: " + aSmsSend.getMessage();
        Log.i("MyApp", log);

        //To do: permission problem
        //send multi recipients
        for (String recipient: aSmsSend.getRecipients()) {
            this.smsManager.sendTextMessage(
                    recipient,
                    null,
                    aSmsSend.getMessage(),
                    null, null); //Note: currently, dont care result
        }

        Toast.makeText(context, log,
                Toast.LENGTH_SHORT).show();

    }
}
