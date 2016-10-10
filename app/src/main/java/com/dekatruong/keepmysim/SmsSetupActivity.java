package com.dekatruong.keepmysim;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dekatruong.keepmysim.dao.SmsSendScheduleDao;
import com.dekatruong.keepmysim.dto.SmsSend;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SmsSetupActivity extends AppCompatActivity {

    private static final int MILIS_PER_MINUTE = 60 * 1000;
    private static final int MILIS_PER_HOUR = MILIS_PER_MINUTE * 60;
    private static final int MILIS_PER_DAY = MILIS_PER_HOUR * 24;

    private static final int REPEAT_ALARM_REQUEST_ID = 111;

    //To Permission Request-Code
    public static class PermissionRequestCodes {
        public static final int SEND_SMS_TO_SEND_SMS = 1;
        public static final int READ_CONTACTS_TO_GET_PHONENUMBER = 2;
    }

    public static class ActivityRequestCodes {
        static final int PICK_CONTACT_REQUEST = 1;  // The request code
    }

    //GUI
    private EditText editTextMessage;
    private EditText editTextPhone;
    private TextView textViewDate;
    private TextView textViewTime;
    private CheckBox checkBoxIsRepeat;
    private EditText editTextDayNumber;
    private EditText editTextHourNumber;
    private EditText editTextMinuteNumber;
    //private NumberPicker mp;
    //
    DialogFragment mMyDatePickerFragment;
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;

    //Dependency
    private SmsManager smsManager = SmsManager.getDefault();
    private AlarmManager alarmMgr;
    //
    DateFormat mDateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    // Temporary
    private PendingIntent repeatAlarmPendingIntent;

    //DTO
    private SmsSendSchedule mSmsSendSchedule;
    //DAO
    private SmsSendScheduleDao mSmsSendScheduleDao;

//    public static class MyDatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        /**
//         * DialogFragment
//         * @param savedInstanceState
//         * @return
//         */
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//            //Log.i("MyTag", "onCreateDialog");
//
//            // Use the current date as the default date in the picker
//            final Calendar currentDateTime = Calendar.getInstance();
//            int year= currentDateTime.get(Calendar.YEAR);
//            int month= currentDateTime.get(Calendar.MONTH);
//            int day = currentDateTime.get(Calendar.DAY_OF_MONTH);
//
//            //Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(this.getActivity(), this, year, month, day);
//        }
//
//        /**
//         * DatePickerDialog.OnDateSetListener
//         * @param view
//         * @param year
//         * @param month
//         * @param day
//         */
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            //Get info and Update UI
//            Calendar newDate = Calendar.getInstance(); newDate.set(year, month, day);
//
//            ((SmsSetupActivity)this.getActivity()).textViewDate
//                    .setText((new SimpleDateFormat("dd-MM-yyyy")).format(newDate.getTime()));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_setup);

        //Dependency
        mSmsSendScheduleDao = new SmsSendScheduleDao(this);

        //UI
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        this.textViewDate = (TextView) findViewById(R.id.editTextDate);
        this.textViewTime = (TextView) findViewById(R.id.editTextTime);
        this.checkBoxIsRepeat = (CheckBox) findViewById(R.id.checkBoxIsRepeat);
        this.editTextDayNumber = (EditText) findViewById(R.id.editTextDayNumber);
        this.editTextHourNumber = (EditText) findViewById(R.id.editTextHourNumber);
        this.editTextMinuteNumber = (EditText) findViewById(R.id.editTextMinuteNumber);

        //Init UI
        Calendar currentCalendar = Calendar.getInstance();
        this.textViewDate.setText(
                (new SimpleDateFormat("dd-MM-yyyy")).format(currentCalendar.getTime()));
        this.textViewTime.setText(
                (new SimpleDateFormat("HH:mm")).format(currentCalendar.getTime()));
        this.checkBoxIsRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //UI
                    SmsSetupActivity.this.editTextDayNumber.setEnabled(true);
                    SmsSetupActivity.this.editTextHourNumber.setEnabled(true);
                    SmsSetupActivity.this.editTextMinuteNumber.setEnabled(true);

                    //Model

                } else {
                    //UI
                    SmsSetupActivity.this.editTextDayNumber.setEnabled(false);
                    SmsSetupActivity.this.editTextHourNumber.setEnabled(false);
                    SmsSetupActivity.this.editTextMinuteNumber.setEnabled(false);

                    //Model
                }

            }
        });


        //
        //mMyDatePickerFragment = new MyDatePickerFragment();
        //Current Date Time
        mDatePickerDialog = this.createDatePickerDialog(currentCalendar);
        mTimePickerDialog = this.createTimePickerDialog(currentCalendar);

        //Service Locator:
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        /////////////////////////////////
        // When Open by other app
        this.onOpenByOtherApp();
    }

    private DatePickerDialog createDatePickerDialog(Calendar init_calendar) {
        Calendar current_calendar = (null != init_calendar) ? init_calendar : Calendar.getInstance();
        return new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        //UI. To do:
                        SmsSetupActivity.this.textViewDate.setText(
                                (new SimpleDateFormat("dd-MM-yyyy")).format(newDate.getTime())
                        );
                    }

                },
                current_calendar.get(Calendar.YEAR),
                current_calendar.get(Calendar.MONTH),
                current_calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog createTimePickerDialog(Calendar init_calendar) {
        Calendar current_calendar = (null != init_calendar) ? init_calendar : Calendar.getInstance();
        return new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newDate.set(Calendar.MINUTE, minute);

                        String timeString = hourOfDay + ":" + minute; //to do
                        //UI. To do
                        (SmsSetupActivity.this).textViewTime.setText(
                                (new SimpleDateFormat("HH:mm")).format(newDate.getTime())
                        );
                    }

                },
                current_calendar.get(Calendar.HOUR_OF_DAY),
                current_calendar.get(Calendar.MINUTE),
                true
        );
    }

    private void onOpenByOtherApp() {
        // Get the intent that started this activity
        Intent intent = this.getIntent();
        if (null == intent || null == intent.getAction()) {
            return;
        }

        Uri data = intent.getData();

        //For multi Action.To do: should use Factory
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_SENDTO:
                // Figure out what to do based on the intent type
                if (intent.getType().indexOf("image/") != -1) {
                    // Handle intents with image data ...
                } else if (intent.getType().equals("text/plain")) {
                    // Handle intents with text ...
                    Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
                    this.setResult(Activity.RESULT_OK, result);
                    finish();
                }
                break;
            case Intent.ACTION_SEND:
                // Figure out what to do based on the intent type
                if (intent.getType().indexOf("image/") != -1) {
                    // Handle intents with image data ...
                } else if (intent.getType().equals("text/plain")) {
                    // Handle intents with text ...
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //Task: get LastKnownLocation
            case PermissionRequestCodes.SEND_SMS_TO_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    this.sendSmsNow();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case PermissionRequestCodes.READ_CONTACTS_TO_GET_PHONENUMBER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    //To do:

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case ActivityRequestCodes.PICK_CONTACT_REQUEST: {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // The user picked a contact.
                    String number = this.getPhoneNumberFromIntent(data);
                    this.editTextPhone.setText(number);
                }
                break;
            }
        }
    }

    private String getPhoneNumberFromIntent(Intent data) {
        // The Intent's data Uri identifies which contact was selected.
        // Get the URI that points to the selected contact
        Uri contactUri = data.getData();
        // We only need the NUMBER column, because there will be only one row in the result
        String[] projection = {Phone.NUMBER};

        try {
            // Perform the query on the contact to get the NUMBER column
            // We don't need a selection or sort order (there's only one result for the given URI)
            // CAUTION: The query() method should be called from a separate thread to avoid blocking
            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
            // Consider using CursorLoader to perform the query.
            Cursor cursor = this.getContentResolver()
                    .query(contactUri, projection, null, null, null); //permission problem

            cursor.moveToFirst(); //one itemData only

            Log.i("MyApp", "getPhoneNumberFromIntent");

            // Retrieve the phone number from the NUMBER column
            String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));

            return number;
        } catch (SecurityException ex) {
            //>= Android 6
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS //Location
                    },
                    PermissionRequestCodes.READ_CONTACTS_TO_GET_PHONENUMBER);

            //To do: add to task-Queue after grant permission

            return "(not permission)"; //To do
        }
    }

    public void onClick_buttonPickContact(View view) {
        //Pick pickContact
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers

        this.startActivityForResult(pickContactIntent, ActivityRequestCodes.PICK_CONTACT_REQUEST);
    }

    public void onClick_editTextDate(View view) {
        //To do: set current datetime
        this.mDatePickerDialog.show();
    }

    public void onClick_editTextTime(View view) {
        //To do: set current datetime
        this.mTimePickerDialog.show();
    }

//    public void onClick_buttonOpenDatePicker(View view) {
//
//        mMyDatePickerFragment.show(
//                this.getFragmentManager(),
//                "datePicker");
//
//    }

    public void onClick_buttonSendNow(View view) {

        this.sendSmsNow();

    }

//    public void onCheckedChanged_checkBoxIsRepeat(CompoundButton buttonView, boolean isChecked)
//    {
//        if ( isChecked )
//        {
//            // perform logic
//            Log.i("MyTag", "check");
//        }
//
//    }


    public void onClick_buttonSendAtSchedule(View view) {
        String log = "Sending Message, GUI Input:" +
                "\n phone: " + editTextPhone.getText().toString() +
                "\n message: " + editTextMessage.getText().toString() +
                "\n date: " + textViewDate.getText().toString() +
                "\n time: " + textViewTime.getText().toString();

        Log.i("MyApp", log);

        ////
        Toast.makeText(this, log,
                Toast.LENGTH_SHORT).show();
        /////
        try {
            Calendar selected_calendar = this.getCalendarFromView();
            //from iew data
            String phone = this.editTextPhone.getText().toString();
            String message = this.editTextMessage.getText().toString();
            boolean is_repeat = this.checkBoxIsRepeat.isChecked();
            long input_interval = this.getMilisFromView();

            //build SmsSendSchedule. Note: currently, storage in class
            mSmsSendSchedule = (new SmsSendSchedule())
                    .setRequestId(REPEAT_ALARM_REQUEST_ID) // temporary
                    .setSendingCalendar(selected_calendar)
                    .setRepeating(is_repeat) //Repeat or not
                    .setInterval(input_interval)
                    .setSmsSend(new SmsSend(Arrays.asList(phone), message));

            //Register
            this.sendSMSAtSchedule(mSmsSendSchedule);

            //Storge
            this.mSmsSendScheduleDao.save(mSmsSendSchedule);

        } catch (ParseException e) {
            e.printStackTrace();
            //do nothing
        }
    }


    public void onClick_buttonStopRepeat(View view) {
        Log.i("MyApp", "onClick_buttonStopRepeat");

        //
        //Build PendingIntent that match last PendingIntent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this, //this, //the same Context
                REPEAT_ALARM_REQUEST_ID, //the same request-code
                new Intent(this, SmsSendingAlarmReceiver.class), //the same class
                0 //0 //the same flag
        );

        alarmMgr.cancel(alarmIntent);
    }

    private Calendar getCalendarFromView() throws ParseException {
        // Get data from View. To do: check datetime format after that
        String date_string = this.textViewDate.getText().toString();
        String time_string = this.textViewTime.getText().toString();

        try {
            Date selected_date = (new SimpleDateFormat("dd-MM-yy HH:mm"))
                    .parse(date_string + " " + time_string);

            Calendar selected_calendar = Calendar.getInstance();
            selected_calendar.setTime(selected_date);

            return selected_calendar;

        } catch (ParseException e) {
            //e.printStackTrace();
            //throw Exception
            throw e;
        }
    }

    private long getMilisFromView() {
        //Data from View. To do: verify
        String m = this.editTextMinuteNumber.getText().toString();
        String h = this.editTextHourNumber.getText().toString();
        String d = this.editTextDayNumber.getText().toString();
        // Note: "" mean 0
        int minutes = ("".equals(m)) ? 0 : Integer.parseInt(m);
        int hour = ("".equals(h)) ? 0 : Integer.parseInt(h);
        int day = ("".equals(d)) ? 0 : Integer.parseInt(d);

        Log.i("MyApp", day + " days " + hour + " hours " + minutes + " minutes ");

        return minutes * MILIS_PER_MINUTE
                + hour * MILIS_PER_HOUR
                + day * MILIS_PER_DAY;
    }

    /**
     * @param calendar 1st alarm datetime
     * @param phone
     * @param message
     */
    private void sendSMSAtSchedule(Calendar calendar, String phone, String message) {
        Log.i("MyApp", "sendSMSAtSchedule: " + calendar.getTime().toString());

        //Build Intent that will be sendBroadCast later
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra(SmsSend.EXTRA_KEY_PHONE, phone);
        intent.putExtra(SmsSend.EXTRA_KEY_MESSAGE, message);

        int request_id = (int) System.currentTimeMillis();
        ; //Unique each Schedule. To do
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                request_id, //Unique each unique Schedule
                intent,
                0);

        // Set the alarm to start at approximately 2:00 p.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 21); //12h
//        calendar.set(Calendar.MINUTE, 25); //15m
        //calendar.setTimeZone();

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
//        alarmMgr.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                interval, //Every x ms
//                alarmIntent);

//        this.alarmMgr.setInexactRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + (5 * 1000),
//                5 * 1000, //5s
//                alarmIntent);

        alarmMgr.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmIntent);

    }

    /**
     * @param calendar 1st alarm datetime
     * @param phone
     * @param message
     */
    private void sendSMSAtSchedule(Calendar calendar, String phone, String message, long interval) {
        Log.i("MyApp", "sendSMSAtSchedule: " + calendar.getTime().toString()
                + "\n interval: " + interval);

        //Build Intent that will be sendBroadCast later
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra(SmsSend.EXTRA_KEY_PHONE, phone);
        intent.putExtra(SmsSend.EXTRA_KEY_MESSAGE, message);

        //int request_id = (int) System.currentTimeMillis();; //Unique each Schedule. To do
        int request_id = REPEAT_ALARM_REQUEST_ID; //Temporary, only 1 alarm to control it
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                request_id,
                intent,
                0);

        //Temporary
        repeatAlarmPendingIntent = alarmIntent; //keep to cancel later

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                interval, //Every x ms
                alarmIntent);

    }

    /**
     * @param aSmsSendSchedule
     */
    private void sendSMSAtSchedule(SmsSendSchedule aSmsSendSchedule) {
        //debug
        //Log.d("MyApp", aSmsSendSchedule.getSmsSend().getRecipientsString());

        //Build Intent that will be sendBroadCast later
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                aSmsSendSchedule.getRequestId(), //request_id, Unique each Schedule
                (new Intent(this, SmsSendingAlarmReceiver.class))
                        .putExtra(SmsSendSchedule.EXTRA_KEY_SMSSEND, aSmsSendSchedule.getSmsSend()), //Json data,
                PendingIntent.FLAG_UPDATE_CURRENT); //Note: should use flag that we can update it later

        //Temporary
        aSmsSendSchedule.setRequestPendingIntent(alarmIntent); //keep to cancel later

        if (aSmsSendSchedule.isRepeating()) {
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    aSmsSendSchedule.getSendingCalendar().getTimeInMillis(),
                    aSmsSendSchedule.getInterval(), //Every x ms
                    alarmIntent);
        } else {
            alarmMgr.set(
                    AlarmManager.RTC_WAKEUP,
                    aSmsSendSchedule.getSendingCalendar().getTimeInMillis(),
                    alarmIntent);
        }

    }


    private void sendSmsNow() {
        String log = "Sending Message:\n" +
                " phone: " + editTextPhone.getText().toString() + "\n" +
                " message: " + editTextMessage.getText().toString();

        Log.i("MyApp", log);

        try {
            smsManager.sendTextMessage(
                    editTextPhone.getText().toString(),
                    null,
                    editTextMessage.getText().toString(),
                    null, null);

            Toast.makeText(this, log,
                    Toast.LENGTH_SHORT).show();
        } catch (SecurityException ex) {
            Log.e("MyApp", "permission");
            //>= Android 6
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.SEND_SMS //Location
                    },
                    PermissionRequestCodes.SEND_SMS_TO_SEND_SMS);
        }
    }

}
