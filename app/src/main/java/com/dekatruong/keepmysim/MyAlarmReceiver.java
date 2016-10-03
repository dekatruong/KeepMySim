package com.dekatruong.keepmysim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.dekatruong.keepmysim.dto.SmsSend;

public class MyAlarmReceiver extends BroadcastReceiver {

    private SmsManager smsManager = SmsManager.getDefault();

    public MyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyApp", "onReceive - MyAlarmReceiver");

        Toast.makeText(context, "onReceive - MyAlarmReceiver",
                Toast.LENGTH_SHORT).show();
        //Extras
        String phone = intent.getStringExtra(SmsSend.EXTRA_KEY_PHONE);
        String message = intent.getStringExtra(SmsSend.EXTRA_KEY_MESSAGE);


        this.sendSms(context, phone, message);
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
}
