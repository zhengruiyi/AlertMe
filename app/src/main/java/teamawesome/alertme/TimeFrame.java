package teamawesome.alertme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class TimeFrame extends ActionBarActivity {
    //to store alarm info
    private WeatherAlarm currentAlarm;

    //weekday checkboxes
    private CheckBox weekday, weekend;
    private boolean[] weekdays = {false, false, false, false, false, false, false};

    //time frame checkboxes
    private int numberOfTimeFrameBoxesChecked = 0;
    private CheckBox twelveHour, twentyFourHour;
    private int timeFrame = 0;

    //time of day check boxes
    private int numberOfTimeDayBoxesChecked = 0;
    private CheckBox am, pm;
    private boolean inMorning = false;

    //Alert time seekbar
    private int changedProgress = 0;

    //to restore settings
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlarmDataSingleton.getInstance().size()) {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(alarmIndex);
        } else {
            throw new AssertionError("TimeFrame: Failed to access AlarmDataSingleton list at " + alarmIndex);
        }
        //Seekbar
        SeekBar alertTime = (SeekBar) findViewById(R.id.seekBar4);
        alertTime.setOnSeekBarChangeListener(alertTimeListener);

        addListenerWeekday();
        addListenerWeekend();

        addListenerTimeFrame12();
        addListenerTimeFrame24();

        addListenerAm();
        addListenerPm();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_frame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Alert Time seekbar
    private SeekBar.OnSeekBarChangeListener alertTimeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            changedProgress = progress;
            TextView displayValue = (TextView) findViewById(R.id.seekBarTextView);
            displayValue.setText("" + progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO
        }
    };//end alert time seekbar

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


    //12-hour Checkbox
    public void addListenerTimeFrame12() {

        twelveHour = (CheckBox) findViewById(R.id.checkBox8);

        twelveHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && numberOfTimeFrameBoxesChecked >= 1) {
                    twelveHour.setChecked(false);
                } else {
                    // the checkbox either got unchecked
                    // or there are less than 2 other checkboxes checked
                    // change your counter accordingly
                    if (((CheckBox) v).isChecked()) {
                        numberOfTimeFrameBoxesChecked++;
                    } else {
                        numberOfTimeFrameBoxesChecked--;
                    }
                    timeFrame = 12;
                }
            }
        });
    }//end 12-hour checkbox

    //24-hour check box
    public void addListenerTimeFrame24() {

        twentyFourHour = (CheckBox) findViewById(R.id.checkBox7);

        twentyFourHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && numberOfTimeFrameBoxesChecked >= 1) {
                    twentyFourHour.setChecked(false);
                } else {
                    // the checkbox either got unchecked
                    // or there are less than 2 other checkboxes checked
                    // change your counter accordingly
                    if (((CheckBox) v).isChecked()) {
                        numberOfTimeFrameBoxesChecked++;
                    } else {
                        numberOfTimeFrameBoxesChecked--;
                    }
                    timeFrame = 24;
                }
            }
        });
    }//end 24-hour checkbox

    //AM Checkbox
    public void addListenerAm() {

        am = (CheckBox) findViewById(R.id.checkBox9);

        am.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && numberOfTimeDayBoxesChecked >= 1) {
                    am.setChecked(false);
                } else {
                    // the checkbox either got unchecked
                    // or there are less than 2 other checkboxes checked
                    // change your counter accordingly
                    if (((CheckBox) v).isChecked()) {
                        numberOfTimeDayBoxesChecked++;
                    } else {
                        numberOfTimeDayBoxesChecked--;
                    }
                    inMorning = true;
                }
            }
        });
    }//end am checkbox

    //PM check box
    public void addListenerPm() {

        pm = (CheckBox) findViewById(R.id.checkBox10);

        pm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && numberOfTimeDayBoxesChecked >= 1) {
                    pm.setChecked(false);
                } else {
                    // the checkbox either got unchecked
                    // or there are less than 2 other checkboxes checked
                    // change your counter accordingly
                    if (((CheckBox) v).isChecked()) {
                        numberOfTimeDayBoxesChecked++;
                    } else {
                        numberOfTimeDayBoxesChecked--;
                    }
                    inMorning = false;
                }
            }
        });
    }//end pm checkbox

    public void soundToggle(View v){
        currentAlarm.toggleSound();
    }

    public void vibrateToggle(View v){
        currentAlarm.toggleVibrate();
    }

    public void saveValues(View v){
        currentAlarm.setAlertTime(changedProgress);
        currentAlarm.setDaysSelected(weekdays);
        currentAlarm.setTimeFrame(timeFrame);
        currentAlarm.setAmPm(inMorning);


        //How to do this??????
        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

    public void toAlarmList(View view){

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

}
