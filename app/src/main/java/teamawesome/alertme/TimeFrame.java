package teamawesome.alertme;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import teamawesome.alertme.Background.AlarmBroadcastReceiver;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.AlertMeAlarm;
import teamawesome.alertme.Utility.CalendarWrapper;


public class TimeFrame extends ActionBarActivity {
    //to store alarm info
    private AlertMeAlarm currentAlarm;

    //weekday checkboxes
    private CheckBox weekday, weekend;
    private boolean[] weekdays = {true, true, true, true, true, false, false};

    //time frame checkboxes
    private Switch twelveHour;
    private int timeFrame = 12; //default twelveHour

    //time of day check boxes
    private CheckBox am, pm;
    private boolean inMorning = false;

    //Alert time seekbar
    private int minutes;
    private TimePicker alertTime;
    private TextView output;
    private Button setTime;
    static final int TIME_DIALOG_ID = 1111;
    private int hour;
    private int minute;

    //Sounds
    private CheckBox vibrate;
    private Switch sound;

    //to restore settings
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlertMeMetadataSingleton.getInstance().size()) {
            currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(alarmIndex);
        } else {
            throw new AssertionError("TimeFrame: Failed to access AlertMeMetadataSingleton list at " + alarmIndex);
        }

        //Time Picker
        output = (TextView) findViewById(R.id.timeDisplay);
        /********* display current time on screen Start ********/
        final Calendar c = Calendar.getInstance();
        // Current Hour
        hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        minute = c.get(Calendar.MINUTE);
        // set current time into output textview
        updateTime(hour, minute);
        /********* display current time on screen End ********/
        // Add Button Click Listener
        addButtonClickListener();

        addListenerWeekday();
        addListenerWeekend();

        addListenerTimeFrame12();

        addListenerSound();
        addListenerVibrate();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("hour", hour);
        outState.putInt("minutes", minute);

        outState.putBooleanArray("weekdays", weekdays);
        outState.putBoolean("weekday", weekday.isChecked());
        outState.putBoolean("weekend", weekend.isChecked());

        outState.putBoolean("twelveHour", twelveHour.isChecked());

        outState.putBoolean("am", inMorning);

        outState.putBoolean("sound", sound.isChecked());
        outState.putBoolean("vibrate", vibrate.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        weekdays = savedInstanceState.getBooleanArray("weekdays");

        //weekdays checkboxes
        weekday.setChecked(savedInstanceState.getBoolean("weekday"));
        weekend.setChecked(savedInstanceState.getBoolean("weekend"));

        //timeframe checkboxes
        twelveHour.setChecked(savedInstanceState.getBoolean("twelveHour"));
        if(twelveHour.isChecked()){timeFrame = 12;}
        else {timeFrame = 24;}

        inMorning = savedInstanceState.getBoolean("am");

        sound.setChecked(savedInstanceState.getBoolean("sound"));
        vibrate.setChecked(savedInstanceState.getBoolean("vibrate"));


    }

    @Override
    public void onPause() {
        super.onPause();
        //check boxes
        save(weekday.isChecked(), "weekday");
        save(weekend.isChecked(), "weekend");
        save(twelveHour.isChecked(), "twelveHour");
        save(vibrate.isChecked(), "vibrate");
        //switch
        save(sound.isChecked(), "sound");

        //seekbar
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("am", inMorning);

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check boxes
        weekday.setChecked(load("weekday"));
        weekend.setChecked(load("weekend"));

        //time frame
        twelveHour.setChecked(load("twelveHour"));
        if (twelveHour.isChecked()){timeFrame = 12;}
        else {timeFrame = 24;}

        inMorning = load("am");

        //sounds
        vibrate.setChecked(load("vibrate"));
        sound.setChecked(load("sound"));

    }

    private void save(final boolean isChecked, String saveName) {
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(saveName, isChecked);
        editor.apply();
    }

    private boolean load(String saveName) {
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        return mPrefs.getBoolean(saveName, false);
    }


    public void addButtonClickListener() {

        setTime = (Button) findViewById(R.id.timePicker);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);
        }
        return null;
    }
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour   = hourOfDay;
            minute = minutes;

            updateTime(hour,minute);
        }
    };
    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        inMorning = true;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
            inMorning = false;
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12) {
            timeSet = "PM";
            inMorning = false;
        }
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        output.setText(aTime);
    }

    //Weekday Checkbox
    public void addListenerWeekday() {

        weekday = (CheckBox) findViewById(R.id.weekday);

        weekday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    for (int i = 0; i < 5; i ++){
                        weekdays[i] = true;
                    }
                }
            }
        });
    }//end weekday checkbox

    //Weekend check box
    public void addListenerWeekend() {

        weekend = (CheckBox) findViewById(R.id.weekend);

        weekend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    weekdays[5] = weekdays[6] = true;
                }
            }
        });
    }//end weekend checkbox

    public void showInfo (View v){
        String message = "\"Time Frame\" refers to the length of " +
              "time in a day Alert Me! will check the " +
              "forecast for your specified conditions.";
        Toast.makeText(TimeFrame.this, message, Toast.LENGTH_LONG).show();
    }


    //12-hour Switch
    public void addListenerTimeFrame12() {

        twelveHour = (Switch) findViewById(R.id.daySpanSwitch);

        twelveHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked()) {
                    timeFrame = 12;
                }
                else {
                    timeFrame = 24;
                }
            }
        });
    }//end 12-hour switch

    //Sound switch
    public void addListenerSound() {

        sound = (Switch) findViewById(R.id.switch1);

        sound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked()) {
                   currentAlarm.toggleSound();
                }
            }
        });
    }//end sound switch

    //vibrate Checkbox
    public void addListenerVibrate() {

        vibrate = (CheckBox) findViewById(R.id.checkBox11);

        vibrate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    currentAlarm.toggleVibrate();
                }
            }
        });
    }//end vibrate checkbox

    private void saveInfo (){
        currentAlarm.setAlertTime((int)(hour + (minute/60.0))); //CHANGE TO MINUTES->CLINT FIX EXPECTED INPUT
        currentAlarm.setDaysSelected(weekdays);
        currentAlarm.setTimeFrame(timeFrame);
        currentAlarm.setAmPm(inMorning);
    }

    public void toAlarmList(View view){
        saveInfo();

        // Setting alarm
        CalendarWrapper calendar = new CalendarWrapper();
        Long alarmTime = calendar.getTimeInMillis() + 10 * 1000;
        AlarmBroadcastReceiver.setAlarm(this, 0, alarmTime);
        Toast.makeText(this, "Alarm scheduled for 10 seconds from now", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

}
