package teamawesome.alertme;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

import teamawesome.alertme.Background.AlarmBroadcastReceiver;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.AlertMeAlarm;
import teamawesome.alertme.Utility.CalendarWrapper;


public class TimeFrame extends ActionBarActivity {
    //to store alarm info
    private AlertMeAlarm currentAlarm;
    private int alarmIndex;

    //weekday checkboxes
    private CheckBox weekday, weekend;
    private boolean[] weekdays = {true, true, true, true, true, false, false};
    private ToggleButton monday, tuesday, wednesday, thursday, friday, saturday, sunday;

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

    private static final String TAG = "TAG2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
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

        //weekday toggles
        monday = (ToggleButton) findViewById(R.id.Monday);
        tuesday = (ToggleButton) findViewById(R.id.Tuesday);
        wednesday = (ToggleButton) findViewById(R.id.Wednesday);
        thursday = (ToggleButton) findViewById(R.id.Thursday);
        friday = (ToggleButton) findViewById(R.id.Friday);
        saturday = (ToggleButton) findViewById(R.id.Saturday);
        sunday = (ToggleButton) findViewById(R.id.Sunday);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("hour", hour);
        outState.putInt("minutes", minute);

        outState.putBooleanArray("weekdays", weekdays);
        outState.putBoolean("weekday", weekday.isChecked());
        outState.putBoolean("weekend", weekend.isChecked());
        outState.putBoolean("monday", monday.isChecked());
        outState.putBoolean("tuesday", tuesday.isChecked());
        outState.putBoolean("wednesday", wednesday.isChecked());
        outState.putBoolean("thursday", thursday.isChecked());
        outState.putBoolean("friday", friday.isChecked());
        outState.putBoolean("saturday", saturday.isChecked());
        outState.putBoolean("sunday", sunday.isChecked());

        outState.putBoolean("twelveHour", twelveHour.isChecked());

        outState.putBoolean("am", inMorning);

        outState.putBoolean("sound", sound.isChecked());
        outState.putBoolean("vibrate", vibrate.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        weekdays = savedInstanceState.getBooleanArray("weekdays");

        //weekdays checkboxes
        weekday.setChecked(savedInstanceState.getBoolean("weekday"));
        weekend.setChecked(savedInstanceState.getBoolean("weekend"));
        monday.setChecked((savedInstanceState.getBoolean("monday")));
        tuesday.setChecked((savedInstanceState.getBoolean("tuesday")));
        wednesday.setChecked((savedInstanceState.getBoolean("wednesday")));
        thursday.setChecked((savedInstanceState.getBoolean("thursday")));
        friday.setChecked((savedInstanceState.getBoolean("friday")));
        saturday.setChecked((savedInstanceState.getBoolean("saturday")));
        sunday.setChecked((savedInstanceState.getBoolean("sunday")));

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
        save(monday.isChecked(), "monday");
        save(tuesday.isChecked(), "tuesday");
        save(wednesday.isChecked(), "wednesday");
        save(thursday.isChecked(), "thursday");
        save(friday.isChecked(), "friday");
        save(saturday.isChecked(), "saturday");
        save(sunday.isChecked(), "sunday");
        for (int i = 0; i < 7; i++){
            save(weekdays[i], "weekdays " + i);
        }

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
        monday.setChecked(load("monday"));
        tuesday.setChecked(load("tuesday"));
        wednesday.setChecked(load("wednesday"));
        thursday.setChecked(load("thursday"));
        friday.setChecked(load("friday"));
        saturday.setChecked(load("saturday"));
        sunday.setChecked(load("sunday"));

        for (int i = 0; i < 7; i++){
            weekdays[i] = load("weekdays " + i);
        }

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
                    for (int i = 0; i < 5; i++) {
                        weekdays[i] = true;
                    }
                    monday.setChecked(true);
                    tuesday.setChecked(true);
                    wednesday.setChecked(true);
                    thursday.setChecked(true);
                    friday.setChecked(true);
                } else {
                    for (int i = 0; i < 5; i++) {
                        weekdays[i] = false;
                    }
                    monday.setChecked(false);
                    tuesday.setChecked(false);
                    wednesday.setChecked(false);
                    thursday.setChecked(false);
                    friday.setChecked(false);
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
                    saturday.setChecked(true);
                    sunday.setChecked(true);
                }
                else{
                    weekdays[5] = weekdays[6] = false;
                    saturday.setChecked(false);
                    sunday.setChecked(false);
                }
            }
        });
    }//end weekend checkbox


    public void weekdayChecked(View view){
        int id = view.getId();
        switch (id){
            case R.id.Monday:
                if (monday.isChecked()) {
                    Log.i(TAG, "Monday value: " + weekdays[0]);
                    weekdays[0] = true;
                }
                else{
                    weekdays[0] = false;
                    weekday.setChecked(false);
                    Log.i(TAG, "Monday value: " + weekdays[0]);
                }
                break;
            case R.id.Tuesday:
                if (tuesday.isChecked()) {
                    weekdays[1] = true;
                    Log.i(TAG, "Tuesday value: " + weekdays[1]);
                }
                else{
                    weekdays[1] = false;
                    weekday.setChecked(false);
                    Log.i(TAG, "Tuesday value: " + weekdays[1]);
                }
                break;
            case R.id.Wednesday:
                if (wednesday.isChecked()) {
                    weekdays[2] = true;
                    Log.i(TAG, "Wednesday value: " + weekdays[2]);
                }
                else{
                    weekdays[2] = false;
                    weekday.setChecked(false);
                    Log.i(TAG, "Wednesday value: " + weekdays[2]);
                }
                break;
            case R.id.Thursday:
                if (thursday.isChecked()) {
                    weekdays[3] = true;
                    Log.i(TAG, "Thursday value: " + weekdays[3]);
                }
                else{
                    weekdays[3] = false;
                    weekday.setChecked(false);
                    Log.i(TAG, "Thursday value: " + weekdays[3]);
                }
                break;
            case R.id.Friday:
                if (friday.isChecked()) {
                    weekdays[4] = true;
                    Log.i(TAG, "Friday value: " + weekdays[4]);
                }
                else{
                    weekdays[4] = false;
                    weekday.setChecked(false);
                    Log.i(TAG, "Friday value: " + weekdays[4]);
                }
                break;
            case R.id.Saturday:
                if (saturday.isChecked()) {
                    weekdays[5] = true;
                    Log.i(TAG, "Saturday value: " + weekdays[5]);
                }
                else{
                    weekdays[5] = false;
                    weekend.setChecked(false);
                    Log.i(TAG, "Saturday value: " + weekdays[5]);
                }
                break;
            case R.id.Sunday:
                if (sunday.isChecked()) {
                    weekdays[6] = true;
                    Log.i(TAG, "Sunday value: " + weekdays[6]);
                }
                else{
                    weekdays[1] = false;
                    weekend.setChecked(false);
                    Log.i(TAG, "Sunday value: " + weekdays[6]);
                }
                break;
            default:
                break;
        }
    }

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
                } else {
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
        currentAlarm.setAlertTimeWithMinutes(hour * 60 + minute);
        currentAlarm.setDaysSelected(weekdays);
        currentAlarm.setTimeFrame(timeFrame);
        currentAlarm.setAmPm(inMorning);
    }

    public void toAlarmList(View view){
        saveInfo();
        setAlarms();

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

    private void setAlarms() {
        CalendarWrapper calendar = new CalendarWrapper();
        int todayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int todayIndexInDaysSelected;
        if (todayInWeek == Calendar.SUNDAY) {
            todayIndexInDaysSelected = weekdays.length - 1;
        } else {
            todayIndexInDaysSelected = todayInWeek - 2;
        }

        long alarmTimeInMillis = currentAlarm.getAlertTimeInMillis();
        long alarmTimeToday = calendar.getTodayInMillis() + alarmTimeInMillis;
        boolean timeHasPassed = calendar.getTimeInMillis() > alarmTimeToday;

        if (weekdays[todayIndexInDaysSelected] && !timeHasPassed) {
            AlarmBroadcastReceiver.setAlarm(alarmIndex, alarmTimeToday);
        } else {
            todayIndexInDaysSelected++;
            while (!weekdays[todayIndexInDaysSelected]) {
                todayIndexInDaysSelected++;
            }

            long alarmTimeNextTime;
            switch (todayIndexInDaysSelected) {
                case 0:
                    alarmTimeNextTime = calendar.getNextMondayInMillis() + alarmTimeInMillis;
                    break;
                case 1:
                    alarmTimeNextTime = calendar.getNextTuesdayInMillis() + alarmTimeInMillis;
                    break;
                case 2:
                    alarmTimeNextTime = calendar.getNextWednesdayInMillis() + alarmTimeInMillis;
                    break;
                case 3:
                    alarmTimeNextTime = calendar.getNextThursdayInMillis() + alarmTimeInMillis;
                    break;
                case 4:
                    alarmTimeNextTime = calendar.getNextFridayInMillis() + alarmTimeInMillis;
                    break;
                case 5:
                    alarmTimeNextTime = calendar.getNextSaturdayInMillis() + alarmTimeInMillis;
                    break;
                case 6:
                    alarmTimeNextTime = calendar.getNextSundayInMillis() + alarmTimeInMillis;
                    break;
                default:
                    alarmTimeNextTime = alarmTimeToday;
                    break;
            }
            AlarmBroadcastReceiver.setAlarm(alarmIndex, alarmTimeNextTime);
        }

        // Toast.makeText(this, "Alarm scheduled", Toast.LENGTH_LONG).show();
    }

}
